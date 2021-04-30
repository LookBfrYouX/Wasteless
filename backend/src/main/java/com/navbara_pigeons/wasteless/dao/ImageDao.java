package com.navbara_pigeons.wasteless.dao;

import com.navbara_pigeons.wasteless.entity.Image;
import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;

public interface ImageDao {

  void saveProfileImageToMachine(MultipartFile image, String imageName) throws IOException;

  void deleteProfileImageOnMachine(String imageName) throws IOException;

  byte[] getProfileImageOnMachine(String imageName) throws IOException;

  byte[] getDefaultProfileImage() throws IOException;

  void saveProductImageToMachine(MultipartFile image, String filename) throws IOException;

  void saveProductImageToDb(Image image);
}
