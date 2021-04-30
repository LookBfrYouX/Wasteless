package com.navbara_pigeons.wasteless.service;


import com.navbara_pigeons.wasteless.exception.*;

import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;

public interface ImageService {

  String uploadProfileImage(Long id, MultipartFile image) throws UserNotFoundException, IOException;

  byte[] downloadProfileImage(long id) throws UserNotFoundException, IOException;

  String uploadProductImage(long businessId, long productId, MultipartFile image)
      throws UserNotFoundException, BusinessNotFoundException, ProductNotFoundException, IOException;

  void deleteProductImage(long imageId, long businessId, long productId) throws UserNotFoundException, BusinessNotFoundException, InsufficientPrivilegesException, ProductNotFoundException, ImageNotFoundException;

  void deleteUserImage(long userId);

}
