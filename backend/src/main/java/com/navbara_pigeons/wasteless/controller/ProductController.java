package com.navbara_pigeons.wasteless.controller;

import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

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

}
