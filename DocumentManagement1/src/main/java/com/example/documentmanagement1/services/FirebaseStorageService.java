package com.example.documentmanagement1.services;

import com.google.api.client.util.Value;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.*;
import com.google.firebase.cloud.StorageClient;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

@Service
public class FirebaseStorageService {

    @Value("${firebase.storage.bucket}")
    private String bucketName;

    private Storage storage;

    @PostConstruct
    private void initializeStorage() throws IOException {
        FileInputStream serviceAccount = new FileInputStream("src/main/resources/chessdb-50aec-firebase-adminsdk-um5md-744dec85c7.json");

        StorageOptions options = StorageOptions.newBuilder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

        storage = options.getService();
    }

    public String uploadFile(MultipartFile file) throws IOException {
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        BlobId blobId = BlobId.of(bucketName, fileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(file.getContentType()).build();

        // Upload to Firebase Storage
        storage.create(blobInfo, file.getBytes());

        // Return public download URL
        return getDownloadUrl(fileName);
    }

    private String getDownloadUrl(String fileName) {
        return String.format("https://storage.googleapis.com/%s/%s", bucketName, fileName);
    }
}
