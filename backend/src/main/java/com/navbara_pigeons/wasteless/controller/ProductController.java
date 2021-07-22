package com.navbara_pigeons.wasteless.controller;

import com.navbara_pigeons.wasteless.dto.BasicProductCreationDto;
import com.navbara_pigeons.wasteless.enums.ProductSortByOption;
import com.navbara_pigeons.wasteless.exception.BusinessNotFoundException;
import com.navbara_pigeons.wasteless.exception.InsufficientPrivilegesException;
import com.navbara_pigeons.wasteless.exception.InvalidPaginationInputException;
import com.navbara_pigeons.wasteless.exception.ProductRegistrationException;
import com.navbara_pigeons.wasteless.exception.UserNotFoundException;
import com.navbara_pigeons.wasteless.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * This controller class provides the endpoints for dealing with products. All requests for products
 * listed by businesses are received here.
 */
@RestController
@Slf4j
@RequestMapping("")
@Tag(name = "Product Endpoint", description = "The API endpoint for Product related requests")
public class ProductController {

  private final ProductService productService;

  /**
   * This is the constructor for the ProductController.
   *
   * @param productService The product service that is used by this controller.
   */
  @Autowired
  public ProductController(ProductService productService) {
    this.productService = productService;
  }

  /**
   * This endpoint retrieves a list of all products listed by a particular business (id).
   *
   * @param id            The business ID.
   * @param pagStartIndex The start index of the list to return, implemented for pagination, Can be
   *                      Null. This index is inclusive.
   * @param pagEndIndex   The stop index of the list to return, implemented for pagination, Can be
   *                      Null. This index is inclusive.
   * @param sortBy        Defines the field to be sorted, can be null and defaults to the 'id'
   *                      field.
   * @param isAscending   Boolean value, whether the sort order should be in ascending order. Is not
   *                      required and defaults to True.
   * @return A list of products.
   * @throws UserNotFoundException Handled in ControllerExceptionHandler class.
   * @throws InsufficientPrivilegesException Handled in ControllerExceptionHandler class.
   * @throws BusinessNotFoundException Handled in ControllerExceptionHandler class.
   */
  @GetMapping("/businesses/{id}/products")
  @Operation(summary = "Show a businesses products", description = "Return a paginated/sorted list of a specific businesses products")
  public ResponseEntity<Object> showBusinessCatalogue(
      @Parameter(
          description = "The unique ID number of the business"
      ) @PathVariable long id,
      @Parameter(
          description = "The start index of the list to return, implemented for pagination, Can be "
              + "Null. This index is inclusive."
      ) @RequestParam(required = false) Integer pagStartIndex,
      @Parameter(
          description = "The stop index of the list to return, implemented for pagination, Can be "
              + "Null. This index is inclusive."
      ) @RequestParam(required = false) Integer pagEndIndex,
      @Parameter(
          description = "Defines the field to be sorted, can be null."
      ) @RequestParam(required = false) ProductSortByOption sortBy,
      @Parameter(
          description = "Boolean value, whether the sort order should be in ascending order. Is not"
              + " required and defaults to True."
      ) @RequestParam(required = false, defaultValue = "true") boolean isAscending)
      throws UserNotFoundException, InsufficientPrivilegesException, BusinessNotFoundException, InvalidPaginationInputException {
    log.info("RETRIEVED PRODUCTS FOR BUSINESS: " + id);
    return new ResponseEntity<>(
        this.productService.getProducts(id, pagStartIndex, pagEndIndex, sortBy, isAscending),
        HttpStatus.valueOf(200));
  }

  /**
   * This endpoint is to add a product to the catalog
   *
   * @param id The business ID.
   * @param product The product to be added.
   * @return A ResponseEntity.
   * @throws InsufficientPrivilegesException Handled in ControllerExceptionHandler class.
   * @throws ProductRegistrationException Handled in ControllerExceptionHandler class.
   */
  @PostMapping("/businesses/{id}/products")
  public ResponseEntity<Object> addToCatalogue(
      @PathVariable long id, @RequestBody BasicProductCreationDto product)
      throws InsufficientPrivilegesException, ProductRegistrationException {
    JSONObject response = productService.addProduct(id, product);
    log.info("ADDED NEW PRODUCT, BUSINESS ID " + id + " PRODUCT NAME " + product.getName());
    return new ResponseEntity<>(response, HttpStatus.valueOf(201));
  }
}
