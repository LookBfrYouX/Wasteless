package com.navbara_pigeons.wasteless.service;


import com.navbara_pigeons.wasteless.entity.Business;
import com.navbara_pigeons.wasteless.entity.Product;
import com.navbara_pigeons.wasteless.exception.BusinessNotFoundException;
import com.navbara_pigeons.wasteless.exception.BusinessTypeException;
import com.navbara_pigeons.wasteless.exception.ProductRegistrationException;
import net.minidev.json.JSONObject;

import java.util.List;

public interface ProductsService {

    List<Product> getProducts(String businessId) throws BusinessNotFoundException;

    JSONObject addProduct(Product product, Business business) throws BusinessTypeException, ProductRegistrationException;
}
