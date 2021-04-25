package com.navbara_pigeons.wasteless.dao;

import com.navbara_pigeons.wasteless.entity.User;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

/**
 * A Data Access Object so handle Images on the machine.
 */
@Repository
public class ImageDaoImpl implements ImageDao {

  private final String storagePath = "./src/main/resources/images/";

  public void saveProfileImageToMachine(MultipartFile image, String fileName) throws IOException {
    Path destination = Paths.get(storagePath + "user/" + fileName);
    Files.copy(image.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
  }

  public void saveImageNameToDB(User user, String fileName) {

  }
}
