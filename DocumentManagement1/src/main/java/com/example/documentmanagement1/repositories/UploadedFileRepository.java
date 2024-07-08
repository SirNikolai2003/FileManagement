package com.example.documentmanagement1.repositories;
import com.example.documentmanagement1.entities.UploadedFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UploadedFileRepository extends JpaRepository<UploadedFile, Long> {
}
