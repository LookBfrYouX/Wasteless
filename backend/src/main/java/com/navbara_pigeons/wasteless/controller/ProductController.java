package com.navbara_pigeons.wasteless.controller;

import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;
import com.navbara_pigeons.wasteless.exception.ProductRegistrationException;

/**
 * This controller class provides the endpoints for dealing with products. All requests for products
 * listed by businesses are received here.
 */
@Controller
@Slf4j
@RequestMapping("")
public class ProductController {

    /**
     * This endpoint retrieves a list of all products listed by a particular business (id).
     * @param id
     * @return
     */
    @GetMapping("/businesses/{id}/products")
    public ResponseEntity<JSONObject> showAllBusinessProducts(@PathVariable String id) {
        try {
            return new ResponseEntity<>(new JSONObject(), HttpStatus.valueOf(200));
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
    public ResponseEntity<String> showAllBusinessProducts(@PathVariable String id, @RequestBody Product product) {
        try {
            return new ResponseEntity<>("Product created successfully", HttpStatus.valueOf(201));
        } catch (ProductRegistrationException exc) {
            throw new ResponseStatusException(HttpStatus.valueOf(400), "There was some error with the data supplied by the user");
        } catch (Exception exc) {
            throw new ResponseStatusException(HttpStatus.valueOf(500), "Internal Error");
        }
    }

}
