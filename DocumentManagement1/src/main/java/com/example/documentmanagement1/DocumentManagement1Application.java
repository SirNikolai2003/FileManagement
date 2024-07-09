package com.example.documentmanagement1;

import com.example.documentmanagement1.services.FilesStorageService;
import jakarta.annotation.Resource;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DocumentManagement1Application  implements CommandLineRunner {

    @Resource
    FilesStorageService storageService;
    public static void main(String[] args) {
        SpringApplication.run(DocumentManagement1Application.class, args);
    }
    @Override
    public void run(String... arg) throws Exception {
//    storageService.deleteAll();
        storageService.init();
    }

}
