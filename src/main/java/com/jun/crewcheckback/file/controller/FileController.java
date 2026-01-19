package com.jun.crewcheckback.file.controller;

import com.jun.crewcheckback.file.domain.FileType;
import com.jun.crewcheckback.file.dto.FileResponse;
import com.jun.crewcheckback.file.dto.FileUploadResponse;
import com.jun.crewcheckback.file.service.FileService;
import com.jun.crewcheckback.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<FileUploadResponse>> uploadFile(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal UserDetails userDetails) {

        String email = userDetails.getUsername();
        FileUploadResponse response = fileService.uploadFile(file, email);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping(value = "/multiple", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<List<FileUploadResponse>>> uploadFiles(
            @RequestParam("files") List<MultipartFile> files,
            @AuthenticationPrincipal UserDetails userDetails) {

        String email = userDetails.getUsername();
        List<FileUploadResponse> responses = fileService.uploadFiles(files, email);

        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @GetMapping("/{fileId}")
    public ResponseEntity<ApiResponse<FileResponse>> getFile(@PathVariable UUID fileId) {
        FileResponse response = fileService.getFile(fileId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<FileResponse>>> getMyFiles(
            @AuthenticationPrincipal UserDetails userDetails) {

        String email = userDetails.getUsername();
        List<FileResponse> responses = fileService.getFilesByUploader(email);

        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @GetMapping("/my/{fileType}")
    public ResponseEntity<ApiResponse<List<FileResponse>>> getMyFilesByType(
            @PathVariable FileType fileType,
            @AuthenticationPrincipal UserDetails userDetails) {

        String email = userDetails.getUsername();
        List<FileResponse> responses = fileService.getFilesByUploaderAndType(email, fileType);

        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @DeleteMapping("/{fileId}")
    public ResponseEntity<ApiResponse<Void>> deleteFile(
            @PathVariable UUID fileId,
            @AuthenticationPrincipal UserDetails userDetails) {

        String email = userDetails.getUsername();
        fileService.deleteFile(fileId, email);

        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
