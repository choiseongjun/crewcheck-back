package com.jun.crewcheckback.file.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.region.static}")
    private String region;

    public S3UploadResult uploadFile(MultipartFile file, String directory) {
        String originalFilename = file.getOriginalFilename();
        String extension = extractExtension(originalFilename);
        String storedFilename = UUID.randomUUID() + extension;
        String s3Key = directory + "/" + storedFilename;

        try {
            byte[] contentBytes;
            String contentType = file.getContentType();
            long contentLength;

            if (isImage(contentType)) {
                // Compress image
                contentBytes = compressImage(file);
                contentLength = contentBytes.length;
            } else {
                contentBytes = file.getBytes();
                contentLength = file.getSize();
            }

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(s3Key)
                    .contentType(contentType)
                    .contentLength(contentLength)
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(contentBytes));

            log.info("File uploaded successfully: {}", s3Key);

            return new S3UploadResult(storedFilename, s3Key);
        } catch (IOException e) {
            log.error("Failed to upload file to S3: {}", e.getMessage());
            throw new RuntimeException("파일 업로드에 실패했습니다.", e);
        }
    }

    private boolean isImage(String contentType) {
        return contentType != null && contentType.startsWith("image/");
    }

    private byte[] compressImage(MultipartFile file) throws IOException {
        java.awt.image.BufferedImage originalImage = javax.imageio.ImageIO.read(file.getInputStream());
        if (originalImage == null) {
            return file.getBytes(); // Cannot read as image, upload original
        }

        // Resize if too big (e.g., width > 1920)
        int targetWidth = 1920;
        if (originalImage.getWidth() > targetWidth) {
            int targetHeight = (int) (originalImage.getHeight() * ((double) targetWidth / originalImage.getWidth()));
            java.awt.Image resultingImage = originalImage.getScaledInstance(targetWidth, targetHeight,
                    java.awt.Image.SCALE_SMOOTH);
            java.awt.image.BufferedImage outputImage = new java.awt.image.BufferedImage(targetWidth, targetHeight,
                    java.awt.image.BufferedImage.TYPE_INT_RGB);
            outputImage.getGraphics().drawImage(resultingImage, 0, 0, null);
            originalImage = outputImage;
        }

        java.io.ByteArrayOutputStream referenceStream = new java.io.ByteArrayOutputStream();
        String formatName = extractExtension(file.getOriginalFilename()).replace(".", "");
        if (formatName.isEmpty())
            formatName = "jpg";

        // Handle transparency for JPEG
        if ("jpg".equalsIgnoreCase(formatName) || "jpeg".equalsIgnoreCase(formatName)) {
            // Convert to RGB if necessary (remove transparency)
            java.awt.image.BufferedImage newImage = new java.awt.image.BufferedImage(
                    originalImage.getWidth(), originalImage.getHeight(), java.awt.image.BufferedImage.TYPE_INT_RGB);
            newImage.createGraphics().drawImage(originalImage, 0, 0, java.awt.Color.WHITE, null);
            originalImage = newImage;
        }

        javax.imageio.ImageIO.write(originalImage, formatName, referenceStream);
        return referenceStream.toByteArray();
    }

    public void deleteFile(String filePath) {
        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(filePath)
                    .build();

            s3Client.deleteObject(deleteObjectRequest);
            log.info("File deleted successfully: {}", filePath);
        } catch (Exception e) {
            log.error("Failed to delete file from S3: {}", e.getMessage());
            throw new RuntimeException("파일 삭제에 실패했습니다.", e);
        }
    }

    private String extractExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf("."));
    }

    public String generateFileUrl(String filePath) {
        return String.format("https://%s.s3.%s.amazonaws.com/%s", bucket, region, filePath);
    }

    public record S3UploadResult(String storedFilename, String filePath) {
    }
}
