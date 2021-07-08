package com.navbara_pigeons.wasteless.dao;

import com.navbara_pigeons.wasteless.entity.Business;
import com.navbara_pigeons.wasteless.entity.Product;
import com.navbara_pigeons.wasteless.exception.ProductNotFoundException;
import com.navbara_pigeons.wasteless.helper.PaginationBuilder;
import java.util.List;


public interface ProductDao {

  void saveProduct(Product product);

  Product getProduct(long productId) throws ProductNotFoundException;

  List<Product> getProducts(Business business, PaginationBuilder pagBuilder);
}
