package com.navbara_pigeons.wasteless.dao;

import com.navbara_pigeons.wasteless.entity.Image;
import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;

public interface ImageDao {

  void saveProductImageToMachine(MultipartFile image, String filename) throws IOException;

  void saveProductImageToDb(Image image);

  void deleteImage(Image image);

  void deleteProductImageFromMachine(String filename) throws IOException;

}
