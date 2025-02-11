package com.example.violations.system.service;

import com.example.violations.system.entity.Violation;
import com.example.violations.system.repository.ViolationRepository;
import io.minio.*;
import io.minio.http.Method;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Service
@Slf4j
public class MinioService {

    private final MinioClient minioClient;
    private final ViolationRepository violationRepository;

    @Value("${minio.bucket-name}")
    private String bucketName;

    public MinioService(
            @Value("${minio.url}") String url,
            @Value("${minio.access-key}") String accessKey,
            @Value("${minio.secret-key}") String secretKey,
            ViolationRepository violationRepository, ViolationRepository violationRepository1) {
        this.violationRepository = violationRepository1;
        this.minioClient = MinioClient.builder()
                .endpoint(url)
                .credentials(accessKey, secretKey)
                .build();
    }
    @SneakyThrows
    public synchronized String uploadFile(MultipartFile file, Violation violation) {

        if (violation.getId() == null) {
            violation = violationRepository.save(violation);
        }

        try (InputStream inputStream = file.getInputStream()) {
            String fileName = "Violation_" + violation.getId();

            fileName = replaceArabicNumbers(fileName);

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .stream(inputStream, file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );
            log.info("File uploaded successfully: {}", fileName);


            String fileUrl;
            try (InputStream is = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .build())) {
                fileUrl =  fileName;
            }

            violation.setCarPhotoUrl(fileUrl);
            violationRepository.save(violation);

            log.info("Minio File URL: {}", fileUrl);
            return fileUrl;
        } catch (Exception e) {
            throw new RuntimeException("Error uploading file to MinIO", e);
        }
    }


    private String replaceArabicNumbers(String input) {
        return input.replace("٠", "0")
                .replace("١", "1")
                .replace("٢", "2")
                .replace("٣", "3")
                .replace("٤", "4")
                .replace("٥", "5")
                .replace("٦", "6")
                .replace("٧", "7")
                .replace("٨", "8")
                .replace("٩", "9");
    }

    @SneakyThrows
    public String createPresignedUrl(String objectName) {
        try {

            String presignedUrl = minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucketName)
                            .object(objectName)
                            .expiry(60 * 60 * 24)
                            .build()
            );
            log.info("Generated Presigned URL for object {}: {}", objectName, presignedUrl);
            return presignedUrl;
        } catch (Exception e) {
            throw new RuntimeException("Error generating presigned URL", e);
        }
    }

}