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
    private int fileCounter = 1;

    @Value("${minio.bucket-name}")
    private String bucketName;

    public MinioService(
            @Value("${minio.url}") String url,
            @Value("${minio.access-key}") String accessKey,
            @Value("${minio.secret-key}") String secretKey,
            ViolationRepository violationRepository) {
        this.minioClient = MinioClient.builder()
                .endpoint(url)
                .credentials(accessKey, secretKey)
                .build();
    }
@SneakyThrows
public synchronized String uploadFile(MultipartFile file, Violation violation) {
    try (InputStream inputStream = file.getInputStream()) {
        String fileName = "Violation" +
                "_" +
                violation.getInspectorName() +
                "_" + fileCounter++;

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


        String presignedUrl = minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .method(Method.GET)
                        .bucket(bucketName)
                        .object(fileName)
                        .expiry(60 * 60 * 24)
                        .build()
        );

        log.info("Presigned URL generated: {}", presignedUrl);
        return presignedUrl;
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

}