package com.example.documentmanagement1.services;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.cloud.StorageClient;

import javax.annotation.PostConstruct;


@Service
public class FCSService {
    @Value("chessdb-50aec.appspot.com")
    private String firebaseBucket;

    public String uploadFiles(MultipartFile file, String nameFile) throws IOException, FirebaseAuthException {
        Bucket bucket = StorageClient.getInstance().bucket(firebaseBucket);
        InputStream content = new ByteArrayInputStream(file.getBytes());
        Blob blob = bucket.create(nameFile.toString(), content, file.getContentType());
        return blob.getMediaLink();
    }
    //    private Storage storage;
//
//
//    @PostConstruct
//    private void initializeStorage() throws IOException {
//        if (storage == null) {
//            storage = StorageOptions.newBuilder()
//                    .setCredentials(GoogleCredentials.getApplicationDefault())
//                    .build()
//                    .getService();
//        }
//    }


//    @Transactional
//    public String uploadFilesAndSaveUrl(MultipartFile file, String nameFile) throws IOException {
//        String originalFileName = file.getOriginalFilename();
//        String fileName = generateFileName(originalFileName);
//        BlobId blobId = BlobId.of(firebaseBucket, fileName);
//        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(file.getContentType()).build();
//
//        try (InputStream content = new ByteArrayInputStream(file.getBytes())) {
//            Blob blob = storage.create(blobInfo, content);
//            String downloadUrl = generateDownloadUrl(firebaseBucket, blob.getName());
//            // Save the downloadUrl to the database or perform any other actions
//            // You can return the URL directly or handle it in another method
//            return downloadUrl;
//        }
//    }

    private String generateFileName(String originalFileName) {
        // Generate unique filename
        return UUID.randomUUID().toString() + "_" + originalFileName;
    }

    private String generateDownloadUrl(String bucketName, String filePath) {
        String encodedPath = encodePath(filePath);
        return String.format("https://firebasestorage.googleapis.com/v0/b/%s/o/%s?alt=media",
                bucketName, encodedPath);
    }

    private String encodePath(String filePath) {
        try {
            return java.net.URLEncoder.encode(filePath, java.nio.charset.StandardCharsets.UTF_8.toString());
        } catch (java.io.UnsupportedEncodingException e) {
            throw new RuntimeException("Error encoding file path: " + e.getMessage(), e);
        }
    }

}
