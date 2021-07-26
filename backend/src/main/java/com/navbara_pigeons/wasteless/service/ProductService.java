package com.navbara_pigeons.wasteless.service;


import com.navbara_pigeons.wasteless.dto.BasicProductCreationDto;
import com.navbara_pigeons.wasteless.dto.BasicProductDto;
import com.navbara_pigeons.wasteless.dto.PaginationDto;
import com.navbara_pigeons.wasteless.entity.Product;
import com.navbara_pigeons.wasteless.enums.ProductSortByOption;
import com.navbara_pigeons.wasteless.exception.BusinessNotFoundException;
import com.navbara_pigeons.wasteless.exception.InsufficientPrivilegesException;
import com.navbara_pigeons.wasteless.exception.InvalidPaginationInputException;
import com.navbara_pigeons.wasteless.exception.ProductNotFoundException;
import com.navbara_pigeons.wasteless.exception.ProductRegistrationException;
import com.navbara_pigeons.wasteless.exception.UserNotFoundException;
import net.minidev.json.JSONObject;

public interface ProductService {

  PaginationDto<BasicProductDto> getProducts(long businessId, Integer pagStartIndex,
      Integer pagEndIndex, ProductSortByOption sortBy, boolean isAscending)
      throws BusinessNotFoundException, InsufficientPrivilegesException, UserNotFoundException, InvalidPaginationInputException;

  JSONObject addProduct(long id, BasicProductCreationDto product)
      throws ProductRegistrationException,
      InsufficientPrivilegesException;

  Product getProduct(long productId) throws ProductNotFoundException;

  void saveProduct(Product product);
}
