package com.navbara_pigeons.wasteless.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Service
public class ImageServiceImpl implements ImageService {

  public final String storageDirectoryPath = "./src/main/resources/images";

  public ResponseEntity uploadToLocalFileSystem(MultipartFile file) {
    String fileName = StringUtils.cleanPath(file.getOriginalFilename());
    Path storageDirectory = Paths.get(storageDirectoryPath);

    if (!Files.exists(storageDirectory)) { // Check if the folder currently exists
      try {
        Files.createDirectories(
            storageDirectory);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    Path destination = Paths.get(storageDirectory.toString() + "/" + fileName);

    try {
      Files.copy(file.getInputStream(), destination,
          StandardCopyOption.REPLACE_EXISTING);

    } catch (IOException e) {
      e.printStackTrace();
    }

    String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
        .path("api/images/getImage/")
        .path(fileName)
        .toUriString();
    return ResponseEntity.ok(fileDownloadUri);
  }
}
