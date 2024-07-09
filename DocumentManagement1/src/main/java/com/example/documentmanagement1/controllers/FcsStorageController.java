package com.example.documentmanagement1.controllers;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import org.antlr.v4.runtime.misc.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.documentmanagement1.services.FCSService;
import com.google.firebase.auth.FirebaseAuthException;

@RestController
@RequestMapping("/upload")
public class FcsStorageController {
    @Autowired
    private	FCSService serv;


    @PostMapping("/files")
    public String guardar (@RequestParam("file") MultipartFile file, String nameFile) throws IOException, FirebaseAuthException {
        if(file.isEmpty()) {
            return "Archivo vacio";
        }
        if (!isValidFileType(file)) {
            return "Only XLS, XLSX, or PDF files are allowed.";
        }
        String name = file.getOriginalFilename();
        return serv.uploadFiles(file, name);
    }


//    @PostMapping("/upload")
//    public ResponseEntity<String> guardar(@RequestParam("file") MultipartFile file) {
//        try {
//            if (file.isEmpty()) {
//                return ResponseEntity.badRequest().body("File is empty");
//            }
//            String downloadUrl = serv.uploadFiles1(file, file.getOriginalFilename());
//            return ResponseEntity.ok().body(downloadUrl);
//        } catch (IOException e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload file: " + e.getMessage());
//        }
//    }
//@PostMapping("/files1")
//public ResponseEntity<String> guardar(@RequestParam("file") MultipartFile file) {
//    try {
//        if (file.isEmpty()) {
//            return ResponseEntity.badRequest().body("File is empty");
//        }
//        String downloadUrl = serv.uploadFilesAndSaveUrl(file, file.getOriginalFilename());
//        return ResponseEntity.ok().body(downloadUrl);
//    } catch (IOException e) {
//        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload file: " + e.getMessage());
//    }
//}
    private boolean isValidFileType(@NotNull MultipartFile file) {
        String contentType = file.getContentType();
        return contentType.equals("application/vnd.ms-excel") // XLS
                || contentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet") // XLSX
                || contentType.equals("application/pdf"); // PDF
    }



}
