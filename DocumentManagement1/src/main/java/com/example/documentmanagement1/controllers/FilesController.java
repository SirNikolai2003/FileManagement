package com.example.documentmanagement1.controllers;
import com.example.documentmanagement1.entities.FileInfo;
import com.example.documentmanagement1.entities.UploadedFile;
import com.example.documentmanagement1.messages.ResponseMessage;
import com.example.documentmanagement1.repositories.UploadedFileRepository;
import com.example.documentmanagement1.services.FilesStorageService;
import com.example.documentmanagement1.services.impl.FilesStorageServiceImpl;
import org.antlr.v4.runtime.misc.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;


import java.util.List;
import java.util.stream.Collectors;
@RestController
public class FilesController {

    @Autowired
    FilesStorageService storageService;
    @Autowired
    private UploadedFileRepository fileRepository;

    @PostMapping("/upload")
    public ResponseEntity<ResponseMessage> uploadFile(@RequestParam("file") List<MultipartFile> files,
                                           @RequestParam("about") String about          ) {
        String message = "";
        String messages = "";

        try {
            if (files.isEmpty()) {
                messages = "Please select a file to upload";
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage(message));
            }

            // Check file type
            for(MultipartFile file : files){
                if (!isValidFileType(file)) {
                    messages = "Only XLS, XLSX, or PDF files are allowed";
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage(message));
                }
                if (file.getSize() > 10 * 1024 * 1024) { // 10MB limit
                    messages = "File size exceeds the limit of 10MB";
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage(message));
                }
                storageService.save(file);
                UploadedFile uploadedFile = new UploadedFile();
            uploadedFile.setFileName(StringUtils.cleanPath(file.getOriginalFilename()));
            uploadedFile.setFileType(file.getContentType());
            uploadedFile.setAbout(about);

            // Save file metadata to database
           fileRepository.save(uploadedFile);

                message +=  file.getOriginalFilename()+ " ";
                messages = "Uploaded the file successfully: " + message;
            }
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(messages));
        } catch (Exception e) {
            for(MultipartFile file : files){

                message = "Could not upload the file: " + file.getOriginalFilename() + ". Error: " + e.getMessage();
                messages += message;
            }

            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(messages));
        }
    }
    @GetMapping("/about")
    public ResponseEntity<Page<UploadedFile>> findByAbout(
            @RequestParam("about") String about,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        if (about == "" || about.isEmpty()) {
            return ResponseEntity.ok(storageService.findAll(page, size));
        }

        Page<UploadedFile> entities = storageService.findByAbout(about, page, size);

        return new ResponseEntity<>(entities, HttpStatus.OK);
    }

//    @GetMapping("/files")
//    public ResponseEntity<List<FileInfo>> getListFiles() {
//        try {
//            List<FileInfo> fileInfos = storageService.loadAll().map(path -> {
//                String filename = path.getFileName().toString();
//                String url = MvcUriComponentsBuilder
//                        .fromMethodName(FilesController.class, "getFile", path.getFileName().toString()).build().toString();
//
//                return new FileInfo(filename, url);
//            }).collect(Collectors.toList());
//
//            if (fileInfos.isEmpty()) {
//                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
//            }
//
//            return ResponseEntity.status(HttpStatus.OK).body(fileInfos);
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
//    }

    @GetMapping("/about/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> getFile(@PathVariable String filename) {
        Resource file = storageService.load(filename);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }
    @DeleteMapping("/deleteAll")
    public ResponseEntity<ResponseMessage> deleteAllFiles() {
        try {
            storageService.deleteAll();
            String message = "All files have been deleted successfully!";
            fileRepository.deleteAll();
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));
        } catch (Exception e) {
            String message = "Failed to delete files: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
        }
    }

    @PutMapping("/about/{filename:.+}")
    public ResponseEntity<String> updateFile(@PathVariable String filename,
                                             @RequestParam("file") MultipartFile file) {
        try {
            // Update the file
            storageService.update(filename, file);
            String message = "Updated the file: " + filename;
            return ResponseEntity.status(HttpStatus.OK).body(message);
        } catch (Exception e) {
            String message = "Could not update the file: " + filename + ". Error: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(message);
        }
    }
    private boolean isValidFileType(@NotNull MultipartFile file) {
        String contentType = file.getContentType();
        return contentType.equals("application/vnd.ms-excel") // XLS
                || contentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet") // XLSX
                || contentType.equals("application/pdf"); // PDF
    }
}
