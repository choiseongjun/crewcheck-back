package com.jun.crewcheckback.file.domain;

import com.jun.crewcheckback.global.entity.BaseEntity;
import com.jun.crewcheckback.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "upload_files")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UploadFile extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "original_filename", nullable = false)
    private String originalFilename;

    @Column(name = "stored_filename", nullable = false, unique = true)
    private String storedFilename;

    @Column(name = "file_size", nullable = false)
    private Long fileSize;

    @Column(name = "content_type", nullable = false)
    private String contentType;

    @Enumerated(EnumType.STRING)
    @Column(name = "file_type", nullable = false, length = 20)
    private FileType fileType;

    @Column(name = "file_path", nullable = false, length = 500)
    private String filePath;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploader_id", nullable = false)
    private User uploader;

    @Builder
    public UploadFile(String originalFilename, String storedFilename,
                      Long fileSize, String contentType, FileType fileType,
                      String filePath, User uploader) {
        this.originalFilename = originalFilename;
        this.storedFilename = storedFilename;
        this.fileSize = fileSize;
        this.contentType = contentType;
        this.fileType = fileType;
        this.filePath = filePath;
        this.uploader = uploader;
    }
}
