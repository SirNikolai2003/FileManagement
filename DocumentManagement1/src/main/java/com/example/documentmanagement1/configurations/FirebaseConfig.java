package com.example.documentmanagement1.configurations;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

//@Configuration
//public class FirebaseConfig {
//
//    @Bean
//    public FirebaseApp firebaseApp() throws IOException {
//        InputStream serviceAccount = new ClassPathResource("serviceAccountKey.json").getInputStream();
//
//        FirebaseOptions options = new FirebaseOptions.Builder()
//                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
//                .setStorageBucket("chessdb-50aec.appspot.com") // Replace with your storage bucket name
//                .build();
//
//        return FirebaseApp.initializeApp(options);
//    }
//}

//@Configuration
public class FirebaseConfig {


//    @Bean
//    public FirebaseApp firebaseApp() throws IOException {
//        FileInputStream serviceAccount = new FileInputStream("src/main/resources/chessdb-50aec-firebase-adminsdk-um5md-744dec85c7.json");
//
//        FirebaseOptions options = new FirebaseOptions.Builder()
//                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
//                .build();
//
//        return FirebaseApp.initializeApp(options);
//    }

}
