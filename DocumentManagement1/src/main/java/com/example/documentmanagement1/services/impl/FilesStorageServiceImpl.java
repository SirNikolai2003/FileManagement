package com.example.documentmanagement1.services.impl;


import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import com.example.documentmanagement1.entities.UploadedFile;
import com.example.documentmanagement1.repositories.UploadedFileRepository;
import com.example.documentmanagement1.services.FilesStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FilesStorageServiceImpl implements FilesStorageService {

    private final Path root = Paths.get("uploads");
    @Autowired
    private UploadedFileRepository uploadedFileRepository;

    @Override
    public void init() {
        try {
            Files.createDirectories(root);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize folder for upload!");
        }
    }

    @Override
    public void save(MultipartFile file) {
        try {
            Files.copy(file.getInputStream(), this.root.resolve(file.getOriginalFilename()));
        } catch (Exception e) {
            if (e instanceof FileAlreadyExistsException) {
                throw new RuntimeException("A file of that name already exists.");
            }

            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Resource load(String filename) {
        try {
            Path file = root.resolve(filename);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Could not read the file!");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }

    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(root.toFile());
    }

    @Override
    public Stream<Path> loadAll() {
        try {
            Stream<Path> pathStream = Files.walk(this.root, 1).filter(path -> !path.equals(this.root)).map(this.root::relativize);
            return pathStream;
        } catch (IOException e) {
            throw new RuntimeException("Could not load the files!");
        }
    }
    @Override
    public void update(String filename, MultipartFile file) {
        try {
            Path existingFile = root.resolve(filename);

            // Check if the file exists
            if (!Files.exists(existingFile)) {
                throw new RuntimeException("File not found: " + filename);
            }

            // Delete the existing file
            Files.delete(existingFile);

            // Save the new file
            save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to update file: " + filename, e);
        }
    }
    public Page<UploadedFile> findByAbout(String about, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return uploadedFileRepository.findByAbout(about, pageable);
    }

    @Override
    public Page<UploadedFile> findAll( int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return uploadedFileRepository.findAll(pageable);
    }
    public void saveFileInFo(MultipartFile file, String about) throws IOException {
        UploadedFile uploadedFile = new UploadedFile();
        uploadedFile.setFileName(StringUtils.cleanPath(file.getOriginalFilename()));
        uploadedFile.setFileType(file.getContentType());
        uploadedFile.setAbout(about);

        uploadedFileRepository.save(uploadedFile);
    }
    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Please select a file to upload");
        }

        if (!isValidFileType(file)) {
            throw new IllegalArgumentException("Only XLS, XLSX, or PDF files are allowed");
        }

        if (file.getSize() > 10 * 1024 * 1024) {
            throw new IllegalArgumentException("File size exceeds the limit of 10MB");
        }
    }
    public boolean isValidFileType(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null &&
                (contentType.equals("application/vnd.ms-excel") || // XLS
                        contentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet") || // XLSX
                        contentType.equals("application/pdf")); // PDF
    }
}
