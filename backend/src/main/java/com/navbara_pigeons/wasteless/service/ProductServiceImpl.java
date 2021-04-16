package com.navbara_pigeons.wasteless.service;

import com.navbara_pigeons.wasteless.dao.BusinessDao;
import com.navbara_pigeons.wasteless.dao.ProductDao;
import com.navbara_pigeons.wasteless.entity.Business;
import com.navbara_pigeons.wasteless.entity.Product;
import com.navbara_pigeons.wasteless.exception.BusinessNotFoundException;
import com.navbara_pigeons.wasteless.exception.BusinessTypeException;
import com.navbara_pigeons.wasteless.exception.ProductRegistrationException;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * A ProductService Implementation
 */
@Service
public class ProductServiceImpl implements ProductService {

    private final BusinessDao businessDao;

    private final ProductDao productDao;

    /**
     * ProductImplementation constructor that takes autowired parameters and sets up the
     * service for interacting with all business related services.
     *
     * @param businessDao The BusinessDataAccessObject.
     */
     @Autowired
     public ProductServiceImpl(BusinessDao businessDao, ProductDao productDao) {
        this.businessDao = businessDao;
        this.productDao = productDao;
    }


    /**
     * This method retrieves a list of all the products listed by a specific business
     * from the ProductDao given the business ID.
     * @param businessId The ID of the business whose products are to be retrieved.
     * @return productCatalogue A List<Product> of products that are in the business product catalogue.
     * @throws BusinessNotFoundException If the business is not listed in the database.
     */
    @Override
    public List<Product> getProducts(String businessId) throws BusinessNotFoundException {
         Business business = businessDao.getBusinessById(Long.parseLong(businessId));
        return business.getProductsCatalogue();
    }

    @Override
    public JSONObject addProduct(long id, Product product) throws BusinessTypeException, ProductRegistrationException {
        throw new ProductRegistrationException();
    }
}
