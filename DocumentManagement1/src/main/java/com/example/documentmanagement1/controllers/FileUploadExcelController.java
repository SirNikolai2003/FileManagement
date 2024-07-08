package com.example.documentmanagement1.controllers;

import com.example.documentmanagement1.services.ExcelDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/excel")
public class FileUploadExcelController {

    @Autowired
    private ExcelDataService excelDataService;


    @PostMapping("/upload")
    public ResponseEntity<String> uploadExcelFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Please select a file to upload.");
        }

        try {
            excelDataService.importDataFromExcel(file);
            return ResponseEntity.ok("File uploaded successfully. Data imported into database.");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload file: " + e.getMessage());
        }
    }
    @GetMapping("/export")
    public String exportToExcel() {
        excelDataService.exportDataToExcel();
        return "Data exported to Excel successfully!";
    }
}
