package com.navbara_pigeons.wasteless.dao;

import com.navbara_pigeons.wasteless.entity.Business;
import com.navbara_pigeons.wasteless.entity.Product;
import com.navbara_pigeons.wasteless.exception.ProductNotFoundException;
import com.navbara_pigeons.wasteless.helper.PaginationBuilder;
import java.util.List;
import org.springframework.data.util.Pair;


public interface ProductDao {

  void saveProduct(Product product);

  Product getProduct(long productId) throws ProductNotFoundException;

  Pair<List<Product>, Long> getProducts(Business business, PaginationBuilder pagBuilder);
}
