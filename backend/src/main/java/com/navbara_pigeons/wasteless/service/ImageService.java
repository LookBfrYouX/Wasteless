package com.navbara_pigeons.wasteless.service;


import com.navbara_pigeons.wasteless.exception.BusinessNotFoundException;
import com.navbara_pigeons.wasteless.exception.ImageNotFoundException;
import com.navbara_pigeons.wasteless.exception.InsufficientPrivilegesException;
import com.navbara_pigeons.wasteless.exception.ProductNotFoundException;
import com.navbara_pigeons.wasteless.exception.UserNotFoundException;
import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;

public interface ImageService {

  void uploadProductImage(long businessId, long productId, MultipartFile image)
      throws UserNotFoundException, BusinessNotFoundException, ProductNotFoundException, IOException, ImageNotFoundException;

  void changePrimaryImage(long businessId, long productId, long imageId)
      throws UserNotFoundException, BusinessNotFoundException, ProductNotFoundException, ImageNotFoundException;

  void deleteProductImage(long imageId, long businessId, long productId)
      throws UserNotFoundException, BusinessNotFoundException, InsufficientPrivilegesException, ProductNotFoundException, ImageNotFoundException, IOException;
}
