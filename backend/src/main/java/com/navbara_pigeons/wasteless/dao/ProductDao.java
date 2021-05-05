package com.navbara_pigeons.wasteless.dao;

import com.navbara_pigeons.wasteless.entity.Product;
import com.navbara_pigeons.wasteless.exception.ProductNotFoundException;


public interface ProductDao {

  void saveProduct(Product product);

  Product getProduct(long productId) throws ProductNotFoundException;
}
