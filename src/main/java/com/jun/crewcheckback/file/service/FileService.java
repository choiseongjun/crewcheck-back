package com.jun.crewcheckback.file.service;

import com.jun.crewcheckback.file.domain.FileType;
import com.jun.crewcheckback.file.domain.UploadFile;
import com.jun.crewcheckback.file.dto.FileResponse;
import com.jun.crewcheckback.file.dto.FileUploadResponse;
import com.jun.crewcheckback.file.repository.UploadFileRepository;
import com.jun.crewcheckback.user.domain.User;
import com.jun.crewcheckback.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FileService {

    private final S3Service s3Service;
    private final UploadFileRepository uploadFileRepository;
    private final UserRepository userRepository;

    private static final List<String> IMAGE_TYPES = List.of("image/jpeg", "image/png", "image/gif", "image/webp", "image/bmp");
    private static final List<String> VIDEO_TYPES = List.of("video/mp4", "video/quicktime", "video/x-msvideo", "video/webm");
    private static final List<String> AUDIO_TYPES = List.of("audio/mpeg", "audio/wav", "audio/ogg", "audio/aac");

    @Transactional
    public FileUploadResponse uploadFile(MultipartFile file, String email) {
        User uploader = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        FileType fileType = determineFileType(file.getContentType());
        String directory = getDirectoryByFileType(fileType);

        S3Service.S3UploadResult uploadResult = s3Service.uploadFile(file, directory);

        UploadFile uploadFile = UploadFile.builder()
                .originalFilename(file.getOriginalFilename())
                .storedFilename(uploadResult.storedFilename())
                .fileSize(file.getSize())
                .contentType(file.getContentType())
                .fileType(fileType)
                .filePath(uploadResult.filePath())
                .uploader(uploader)
                .build();

        UploadFile savedFile = uploadFileRepository.save(uploadFile);
        String fileUrl = s3Service.generateFileUrl(savedFile.getFilePath());

        return FileUploadResponse.from(savedFile, fileUrl);
    }

    @Transactional
    public List<FileUploadResponse> uploadFiles(List<MultipartFile> files, String email) {
        return files.stream()
                .map(file -> uploadFile(file, email))
                .toList();
    }

    public FileResponse getFile(UUID fileId) {
        UploadFile file = uploadFileRepository.findByIdAndNotDeleted(fileId)
                .orElseThrow(() -> new RuntimeException("파일을 찾을 수 없습니다."));

        String fileUrl = s3Service.generateFileUrl(file.getFilePath());
        return FileResponse.from(file, fileUrl);
    }

    public List<FileResponse> getFilesByUploader(String email) {
        User uploader = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        return uploadFileRepository.findByUploaderIdAndNotDeleted(uploader.getId())
                .stream()
                .map(file -> FileResponse.from(file, s3Service.generateFileUrl(file.getFilePath())))
                .toList();
    }

    public List<FileResponse> getFilesByUploaderAndType(String email, FileType fileType) {
        User uploader = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        return uploadFileRepository.findByUploaderIdAndFileTypeAndNotDeleted(uploader.getId(), fileType)
                .stream()
                .map(file -> FileResponse.from(file, s3Service.generateFileUrl(file.getFilePath())))
                .toList();
    }

    @Transactional
    public void deleteFile(UUID fileId, String email) {
        User requester = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        UploadFile file = uploadFileRepository.findByIdAndNotDeleted(fileId)
                .orElseThrow(() -> new RuntimeException("파일을 찾을 수 없습니다."));

        if (!file.getUploader().getId().equals(requester.getId())) {
            throw new RuntimeException("파일을 삭제할 권한이 없습니다.");
        }

        s3Service.deleteFile(file.getFilePath());
        file.delete();
    }

    private FileType determineFileType(String contentType) {
        if (contentType == null) {
            return FileType.DOCUMENT;
        }

        if (IMAGE_TYPES.contains(contentType)) {
            return FileType.IMAGE;
        } else if (VIDEO_TYPES.contains(contentType)) {
            return FileType.VIDEO;
        } else if (AUDIO_TYPES.contains(contentType)) {
            return FileType.AUDIO;
        }

        return FileType.DOCUMENT;
    }

    private String getDirectoryByFileType(FileType fileType) {
        return switch (fileType) {
            case IMAGE -> "images";
            case VIDEO -> "videos";
            case AUDIO -> "audios";
            case DOCUMENT -> "documents";
        };
    }
}
