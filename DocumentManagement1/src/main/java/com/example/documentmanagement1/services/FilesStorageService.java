package com.example.documentmanagement1.services;
import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.Stream;

import com.example.documentmanagement1.entities.UploadedFile;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;
public interface FilesStorageService {
    public void init();

    public void save(MultipartFile file);

    public Resource load(String filename);

    public void deleteAll();

    public Stream<Path> loadAll();
    void update(String filename, MultipartFile file);
    public Page<UploadedFile> findByAbout(String about, int page, int size);
    public Page<UploadedFile> findAll( int page, int size);
    public void saveFileInFo(MultipartFile file, String about) throws IOException;
}
