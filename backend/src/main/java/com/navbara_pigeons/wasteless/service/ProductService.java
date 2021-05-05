package com.navbara_pigeons.wasteless.service;


import com.navbara_pigeons.wasteless.dto.BasicProductCreationDto;
import com.navbara_pigeons.wasteless.dto.BasicProductDto;
import com.navbara_pigeons.wasteless.entity.Product;
import com.navbara_pigeons.wasteless.exception.BusinessNotFoundException;
import com.navbara_pigeons.wasteless.exception.InsufficientPrivilegesException;
import com.navbara_pigeons.wasteless.exception.ProductForbiddenException;
import com.navbara_pigeons.wasteless.exception.ProductNotFoundException;
import com.navbara_pigeons.wasteless.exception.ProductRegistrationException;
import com.navbara_pigeons.wasteless.exception.UserNotFoundException;
import java.util.List;

public interface ProductService {

  List<BasicProductDto> getProducts(long businessId)
      throws BusinessNotFoundException, InsufficientPrivilegesException, UserNotFoundException;

  void addProduct(long id, BasicProductCreationDto product) throws ProductRegistrationException,
      ProductForbiddenException;

  Product getProduct(long productId) throws ProductNotFoundException;

  void saveProduct(Product product);

}
