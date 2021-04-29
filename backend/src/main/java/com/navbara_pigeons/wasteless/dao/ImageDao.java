package com.navbara_pigeons.wasteless.dao;

import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;

public interface ImageDao {

  void saveProfileImageToMachine(MultipartFile image, String imageName) throws IOException;

  void deleteProfileImageOnMachine(String imageName) throws IOException;

  byte[] getProfileImageOnMachine(String imageName) throws IOException;

  byte[] getDefaultProfileImage() throws IOException;
}
