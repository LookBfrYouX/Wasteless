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

  public final String storagePath = "./src/main/resources/images/";

  public ResponseEntity<String> uploadProfileImage(MultipartFile image) {
    String fileName = StringUtils.cleanPath(image.getOriginalFilename());
    Path destination = Paths.get(storagePath + "user/" + fileName);

    try {
      Files.copy(image.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException e) {
      e.printStackTrace();
    }

    String imagePathURI = ServletUriComponentsBuilder.fromCurrentContextPath()
        .path("/images/user/download/")
        .path(fileName)
        .toUriString();
    return ResponseEntity.ok(imagePathURI);
  }
}
