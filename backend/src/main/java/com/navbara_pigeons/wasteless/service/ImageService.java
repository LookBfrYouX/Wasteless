package com.navbara_pigeons.wasteless.service;


import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface ImageService {

  ResponseEntity uploadToLocalFileSystem(MultipartFile file);
}
