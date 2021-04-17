package com.navbara_pigeons.wasteless.service;

import com.navbara_pigeons.wasteless.dao.BusinessDao;
import com.navbara_pigeons.wasteless.dao.ProductDao;
import com.navbara_pigeons.wasteless.dao.UserDao;
import com.navbara_pigeons.wasteless.entity.Business;
import com.navbara_pigeons.wasteless.entity.Product;
import com.navbara_pigeons.wasteless.entity.User;
import com.navbara_pigeons.wasteless.exception.BusinessNotFoundException;
import com.navbara_pigeons.wasteless.exception.ProductForbiddenException;
import com.navbara_pigeons.wasteless.exception.ProductRegistrationException;
import com.navbara_pigeons.wasteless.exception.UserNotFoundException;
import com.navbara_pigeons.wasteless.security.model.BasicUserDetails;
import com.navbara_pigeons.wasteless.validation.ProductServiceValidation;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * A ProductService Implementation
 */
@Service
public class ProductServiceImpl implements ProductService {

    private final BusinessDao businessDao;
    private final ProductDao productDao;
    private final UserDao userDao;

    /**
     * ProductImplementation constructor that takes autowired parameters and sets up the
     * service for interacting with all business related services.
     *
     * @param businessDao The BusinessDataAccessObject.
     */
     @Autowired
     public ProductServiceImpl(BusinessDao businessDao, ProductDao productDao, UserDao userDao) {
        this.businessDao = businessDao;
        this.productDao = productDao;
        this.userDao = userDao;
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

  /**
   * This method adds a new product to a specific business catalogue
   * @param businessId The ID of the business.
   * @param jsonProduct JSONObject of the product to be added.
   * @return productCatalogue A List<Product> of products that are in the business product catalogue.
   * @throws BusinessNotFoundException If the business is not listed in the database.
   */
    @Override
    public void addProduct(long businessId, JSONObject jsonProduct) throws ProductRegistrationException,
        ProductForbiddenException {
      // Throw 400 if bad request, 403 if user is not business admin
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      String username = ((BasicUserDetails) authentication.getPrincipal()).getUsername();
      User user;
      Business business;
      try {
        user = userDao.getUserByEmail(username);
        business = businessDao.getBusinessById(businessId);
      } catch (UserNotFoundException exc) {
        throw new ProductRegistrationException();
      } catch (BusinessNotFoundException exc) {
        throw new ProductRegistrationException();
      }
      boolean businessAdmin = false;
      if (business.getPrimaryAdministratorId() == user.getId()) {
        businessAdmin = true;
      }
      for (User admin : business.getAdministrators()) {
        if (admin.getId() == user.getId()) {
          businessAdmin = true;
        }
      }
      if (!businessAdmin) {
        throw new ProductForbiddenException();
      }
      // Convert jsonProduct to Product, cant be done in controller as we need to accept id in POST
      Product product = new Product();
      product.setName(jsonProduct.getOrDefault("name", "").toString());
      product.setDescription(jsonProduct.getOrDefault("description", "").toString());
      product.setRecommendedRetailPrice(Double.parseDouble(jsonProduct.getOrDefault("recommendedRetailPrice", 0.0).toString()));
      // Product validation
      if (!ProductServiceValidation.isProductValid(product)) {
        throw new ProductRegistrationException();
      }
      product.setCreated(ZonedDateTime.now(ZoneOffset.UTC));
      productDao.saveProduct(product);
    }
}
