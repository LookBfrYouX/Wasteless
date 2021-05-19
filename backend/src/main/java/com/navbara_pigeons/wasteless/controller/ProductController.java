package com.navbara_pigeons.wasteless.controller;

import com.navbara_pigeons.wasteless.dto.BasicProductCreationDto;
import com.navbara_pigeons.wasteless.exception.BusinessNotFoundException;
import com.navbara_pigeons.wasteless.exception.InsufficientPrivilegesException;
import com.navbara_pigeons.wasteless.exception.ForbiddenException;
import com.navbara_pigeons.wasteless.exception.ProductRegistrationException;
import com.navbara_pigeons.wasteless.exception.UserNotFoundException;
import com.navbara_pigeons.wasteless.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
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
   *
   * @param id The ID of the business whose products are to be displayed
   * @return response A JSONObject containing the product information of all products listed for the
   * business.
   */
  @GetMapping("/businesses/{id}/products")
  public ResponseEntity<Object> showBusinessCatalogue(@PathVariable long id) {
    try {
      log.info("RETRIEVED PRODUCTS FOR BUSINESS: " + id);
      return new ResponseEntity<>(this.productService.getProducts(id), HttpStatus.valueOf(200));
    } catch (InsufficientPrivilegesException exc) {
      log.info("INSUFFICIENT PRIVILEGES GETTING BUSINESS WITH ID " + id + " " + exc.getMessage());
      throw new ResponseStatusException(HttpStatus.valueOf(403), "Insufficient Privileges");
    } catch (BusinessNotFoundException | UserNotFoundException exc) {
      log.info("USER OR BUSINESS NOT FOUND: " + id + " " + exc.getMessage());
      throw new ResponseStatusException(HttpStatus.valueOf(406), exc.getMessage());
    } catch (Exception exc) {
      log.info("EXCEPTION GETTING PRODUCT CATALOG + " + exc.getMessage());
      throw new ResponseStatusException(HttpStatus.valueOf(500), "Internal Error");
    }
  }

  /**
   * This endpoint is to add a product to the catalog
   *
   * @param id      The id of business
   * @param product The product to be added
   */
  @PostMapping("/businesses/{id}/products")
  public ResponseEntity<Object> addToCatalogue(@PathVariable long id,
                                                   @RequestBody BasicProductCreationDto product) {
    try {
      JSONObject response = productService.addProduct(id, product);
      log.info("ADDED NEW PRODUCT, BUSINESS ID " + id + " PRODUCT NAME " + product.getName());
      return new ResponseEntity<>(response, HttpStatus.valueOf(201));
    } catch (ProductRegistrationException exc) {
      log.info("ADDING NEW PRODUCT, BUSINESS ID " + id + " BAD INFO " + exc.getMessage());
      throw new ResponseStatusException(HttpStatus.valueOf(400),
          "There was some error with the data supplied by the user");
    } catch (ForbiddenException exc) {
      log.info("ADDING NEW PRODUCT, BUSINESS ID " + id + " FORBIDDEN " + exc.getMessage());
      throw new ResponseStatusException(HttpStatus.valueOf(403), "Forbidden");
    } catch (Exception exc) {
      log.info("ADDING NEW PRODUCT, BUSINESS ID " + id + " ERROR " + exc.getMessage());
      throw new ResponseStatusException(HttpStatus.valueOf(500), "Internal Error");
    }
  }

}