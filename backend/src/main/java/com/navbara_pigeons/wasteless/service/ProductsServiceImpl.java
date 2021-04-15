package com.navbara_pigeons.wasteless.service;

import com.navbara_pigeons.wasteless.dao.AddressDao;
import com.navbara_pigeons.wasteless.dao.BusinessDao;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * A ProductService Implementation
 */
@Service
public class ProductsServiceImpl  implements ProductsService{

    private final BusinessDao businessDao;

    private final ProductDao productDao;

    /**
     * ProductImplementation constructor that takes autowired parameters and sets up the
     * service for interacting with all business related services.
     *
     * @param businessDao The BusinessDataAccessObject.
     */
     @Autowired
     public ProductsServiceImpl(BusinessDao businessDao, ProductDao productDao) {
        this.businessDao = businessDao;
        this.productDao = productDao;
    }




}
