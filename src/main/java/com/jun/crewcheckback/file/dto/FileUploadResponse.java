package com.jun.crewcheckback.file.dto;

import com.jun.crewcheckback.file.domain.FileType;
import com.jun.crewcheckback.file.domain.UploadFile;

import java.util.UUID;

public record FileUploadResponse(
        UUID id,
        String originalFilename,
        String fileUrl,
        Long fileSize,
        FileType fileType
) {
    public static FileUploadResponse from(UploadFile file, String fileUrl) {
        return new FileUploadResponse(
                file.getId(),
                file.getOriginalFilename(),
                fileUrl,
                file.getFileSize(),
                file.getFileType()
        );
    }
}
