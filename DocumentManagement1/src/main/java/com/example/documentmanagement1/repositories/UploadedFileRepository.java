package com.example.documentmanagement1.repositories;
import com.example.documentmanagement1.entities.UploadedFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UploadedFileRepository extends JpaRepository<UploadedFile, Long> {
    Page<UploadedFile> findByAbout(String about, Pageable pageable);

}
