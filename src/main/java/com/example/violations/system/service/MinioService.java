package com.example.violations.system.service;

import com.example.violations.system.entity.CarImage;
import com.example.violations.system.entity.Violation;
import com.example.violations.system.repository.CarImageRepository;
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
import java.util.ArrayList;
import java.util.List;

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
            ViolationRepository violationRepository, CarImageRepository carImageRepository) {
        this.violationRepository = violationRepository;
        this.minioClient = MinioClient.builder()
                .endpoint(url)
                .credentials(accessKey, secretKey)
                .build();
    }
    @SneakyThrows
    public synchronized List<String> uploadFile(List<MultipartFile> files, Violation violation) {
        List<String> uploadedFileNames = new ArrayList<>();

        if (violation.getId() == null) {
            violation = violationRepository.save(violation); // Ensure violation is saved for ID
        }

        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                log.error("File is empty: {}", file.getOriginalFilename());
                continue;
            }
            try (InputStream inputStream = file.getInputStream()) {
                String fileName = "Violation_" + violation.getId() + "_" + System.currentTimeMillis();
                fileName = replaceArabicNumbers(fileName);

                log.info("Uploading file: {}", fileName);
                minioClient.putObject(
                        PutObjectArgs.builder()
                                .bucket(bucketName)
                                .object(fileName)
                                .stream(inputStream, file.getSize(), -1)
                                .contentType(file.getContentType())
                                .build()
                );
                uploadedFileNames.add(fileName);
                log.info("File uploaded successfully: {}", fileName);
            } catch (Exception e) {
                log.error("Error uploading file: {}", e.getMessage());
                throw new RuntimeException("Error uploading file to MinIO", e);
            }
        }

        return uploadedFileNames;
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

    public String createPresignedUrl(String objectName) {
        try {
            log.debug("Generating presigned URL for bucket: {} and object: {}", bucketName, objectName);

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
            log.error("Error generating presigned URL for object: {}", objectName, e);
            throw new RuntimeException("Error generating presigned URL", e);
        }
    }

}