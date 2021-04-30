package com.navbara_pigeons.wasteless.service;


import com.navbara_pigeons.wasteless.entity.Business;
import com.navbara_pigeons.wasteless.entity.Product;
import com.navbara_pigeons.wasteless.exception.*;
import net.minidev.json.JSONObject;

import java.util.List;

public interface ProductService {

    List<Product> getProducts(String businessId) throws BusinessNotFoundException, InsufficientPrivilegesException, UserNotFoundException;

    void addProduct(long id, JSONObject product) throws ProductRegistrationException,
        ProductForbiddenException;

    Product getProduct(long productId) throws ProductNotFoundException;
}
