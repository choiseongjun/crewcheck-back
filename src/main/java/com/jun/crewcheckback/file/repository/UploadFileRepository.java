package com.jun.crewcheckback.file.repository;

import com.jun.crewcheckback.file.domain.FileType;
import com.jun.crewcheckback.file.domain.UploadFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UploadFileRepository extends JpaRepository<UploadFile, UUID> {

    @Query("SELECT f FROM UploadFile f WHERE f.id = :id AND f.deletedYn = 'N'")
    Optional<UploadFile> findByIdAndNotDeleted(@Param("id") UUID id);

    @Query("SELECT f FROM UploadFile f WHERE f.uploader.id = :uploaderId AND f.deletedYn = 'N' ORDER BY f.createdAt DESC")
    List<UploadFile> findByUploaderIdAndNotDeleted(@Param("uploaderId") UUID uploaderId);

    @Query("SELECT f FROM UploadFile f WHERE f.uploader.id = :uploaderId AND f.fileType = :fileType AND f.deletedYn = 'N' ORDER BY f.createdAt DESC")
    List<UploadFile> findByUploaderIdAndFileTypeAndNotDeleted(@Param("uploaderId") UUID uploaderId, @Param("fileType") FileType fileType);

    Optional<UploadFile> findByStoredFilename(String storedFilename);
}
