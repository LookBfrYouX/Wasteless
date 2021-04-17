package com.navbara_pigeons.wasteless.controller;

import com.navbara_pigeons.wasteless.entity.Product;
import com.navbara_pigeons.wasteless.exception.BusinessNotFoundException;
import com.navbara_pigeons.wasteless.exception.ProductForbiddenException;
import com.navbara_pigeons.wasteless.exception.ProductRegistrationException;
import com.navbara_pigeons.wasteless.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * This controller class provides the endpoints for dealing with products. All requests for products
 * listed by businesses are received here.
 */
@Controller
@Slf4j
@RequestMapping("")
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * This endpoint retrieves a list of all products listed by a particular business (id).
     * @param id The ID of the business whose products are to be displayed
     * @return response A JSONObject containing the product information of all products listed for the business.
     */
    @GetMapping("/businesses/{id}/products")
    public ResponseEntity<List> showBusinessCatalogue(@PathVariable String id) {
        try {
            List<Product> response = productService.getProducts(id);
            return new ResponseEntity<>(response, HttpStatus.valueOf(200));
        } catch (BusinessNotFoundException exc) {
            throw new ResponseStatusException(HttpStatus.valueOf(406), exc.getMessage());
        } catch (Exception exc) {
            throw new ResponseStatusException(HttpStatus.valueOf(500), "Internal Error");
        }
    }

    /**
     * This endpoint is to add a product to the catalog
     * @param id The id of business
     * @param product The product to be added
     */
    @PostMapping("/businesses/{id}/products")
    public ResponseEntity<String> addToCatalogue(@PathVariable String id, @RequestBody Product product) {
        try {
            productService.addProduct(Long.parseLong(id), product);
            return new ResponseEntity<>("Product created successfully", HttpStatus.valueOf(201));
        } catch (ProductRegistrationException exc) {
            throw new ResponseStatusException(HttpStatus.valueOf(400), "There was some error with the data supplied by the user");
        } catch (ProductForbiddenException exc) {
            throw new ResponseStatusException(HttpStatus.valueOf(403), "Forbidden");
        } catch (Exception exc) {
            throw new ResponseStatusException(HttpStatus.valueOf(500), "Internal Error");
        }
    }

}
