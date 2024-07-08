package com.example.documentmanagement1.controllers;

import com.example.documentmanagement1.entities.UploadedFile;
import com.example.documentmanagement1.repositories.UploadedFileRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.antlr.v4.runtime.misc.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@RestController
@RequestMapping("/api/files")
public class FileUploadController {
    @Autowired
    private UploadedFileRepository fileRepository;
    private static final String DOWNLOAD_DIR = "D:/CongTy";

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        // Validate file
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Please select a file to upload.");
        }

        // Validate file type
        if (!isValidFileType(file)) {
            return ResponseEntity.badRequest().body("Only XLS, XLSX, or PDF files are allowed.");
        }

        // Save file to database
        try {
            UploadedFile uploadedFile = new UploadedFile();
            uploadedFile.setFileName(StringUtils.cleanPath(file.getOriginalFilename()));
            uploadedFile.setFileType(file.getContentType());

            // Save file metadata to database
            uploadedFile = fileRepository.save(uploadedFile);

            // Handle file content storage (not covered here)

            return ResponseEntity.ok("File uploaded successfully with ID: " + uploadedFile.getId());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload file.");
        }
    }
    @GetMapping("/download/{fileName:.+}")
    public ResponseEntity<InputStreamResource> downloadFile(@PathVariable String fileName, HttpServletRequest request) {
        try {
            File file = new File(DOWNLOAD_DIR + File.separator + fileName);

            if (!file.exists()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
            headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(file.length())
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    // Method to validate file type
    private boolean isValidFileType(@NotNull MultipartFile file) {
        String contentType = file.getContentType();
        return contentType.equals("application/vnd.ms-excel") // XLS
                || contentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet") // XLSX
                || contentType.equals("application/pdf"); // PDF
    }
}
