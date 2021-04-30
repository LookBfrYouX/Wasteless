package com.navbara_pigeons.wasteless.service;


import com.navbara_pigeons.wasteless.exception.BusinessNotFoundException;
import com.navbara_pigeons.wasteless.exception.ProductNotFoundException;
import com.navbara_pigeons.wasteless.exception.UserNotFoundException;
import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;

public interface ImageService {

  String uploadProfileImage(Long id, MultipartFile image) throws UserNotFoundException, IOException;

  byte[] downloadProfileImage(long id) throws UserNotFoundException, IOException;

  String uploadProductImage(long businessId, long productId, MultipartFile image)
      throws UserNotFoundException, BusinessNotFoundException, ProductNotFoundException, IOException;

  void changePrimaryImage(long businessId, long productId, long imageId)
      throws UserNotFoundException, BusinessNotFoundException, ProductNotFoundException;
}
