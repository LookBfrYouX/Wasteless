package com.navbara_pigeons.wasteless.service;


import com.navbara_pigeons.wasteless.exception.UserNotFoundException;
import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;

public interface ImageService {

  String uploadProfileImage(MultipartFile image) throws UserNotFoundException, IOException;
}
