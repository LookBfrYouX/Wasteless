package com.navbara_pigeons.wasteless.dao;

import com.navbara_pigeons.wasteless.entity.User;
import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;

public interface ImageDao {

  void saveProfileImageToMachine(MultipartFile image, String fileName) throws IOException;

  void saveImageNameToDB(User user, String fileName);
}
