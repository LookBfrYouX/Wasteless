package com.navbara_pigeons.wasteless.dao;

import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;

public interface ImageDao {
  void saveProfileImage(MultipartFile image, String fileName) throws IOException;
}
