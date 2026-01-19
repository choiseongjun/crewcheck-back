package com.jun.crewcheckback.file.dto;

import com.jun.crewcheckback.file.domain.FileType;
import com.jun.crewcheckback.file.domain.UploadFile;

import java.time.LocalDateTime;
import java.util.UUID;

public record FileResponse(
        UUID id,
        String originalFilename,
        String fileUrl,
        Long fileSize,
        String contentType,
        FileType fileType,
        UUID uploaderId,
        LocalDateTime createdAt
) {
    public static FileResponse from(UploadFile file, String fileUrl) {
        return new FileResponse(
                file.getId(),
                file.getOriginalFilename(),
                fileUrl,
                file.getFileSize(),
                file.getContentType(),
                file.getFileType(),
                file.getUploader().getId(),
                file.getCreatedAt()
        );
    }
}
