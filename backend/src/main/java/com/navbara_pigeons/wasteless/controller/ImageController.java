package com.navbara_pigeons.wasteless.controller;

import com.navbara_pigeons.wasteless.exception.BusinessNotFoundException;
import com.navbara_pigeons.wasteless.exception.ImageNotFoundException;
import com.navbara_pigeons.wasteless.exception.InsufficientPrivilegesException;
import com.navbara_pigeons.wasteless.exception.ProductNotFoundException;
import com.navbara_pigeons.wasteless.exception.UserNotFoundException;
import com.navbara_pigeons.wasteless.service.ImageService;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

/** @author Alec Fox, Fletcher Dick, Haruka Ichinose */
@Slf4j
@RestController
public class ImageController {

  private final ImageService imageService;

  /**
   * Image controller constructor
   *
   * @param imageService The Image service to be linked
   */
  public ImageController(@Autowired ImageService imageService) {
    this.imageService = imageService;
  }

  /**
   * Add image to business's product
   *
   * @param businessId id of the business
   * @param productId id of the product
   * @param image image to be added
   * @return The URI for the relative image location
   */
  @PostMapping("/businesses/{businessId}/products/{productId}/images")
  public ResponseEntity<String> uploadProductImage(
      @PathVariable long businessId,
      @PathVariable long productId,
      @RequestParam MultipartFile image) {
    try {
      imageService.uploadProductImage(businessId, productId, image);
      log.info(
          "PRODUCT "
              + productId
              + " SUCCESSFULLY UPLOADED IMAGE "
              + image.getOriginalFilename()
              + " TO BUSINESS "
              + businessId);
      return new ResponseEntity<>(HttpStatus.CREATED);
    } catch (UserNotFoundException exc) {
      log.error("USER NOT FOUND ERROR: " + productId);
      throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "The user does not exist");
    } catch (BusinessNotFoundException exc) {
      log.error("BUSINESS NOT FOUND ERROR: " + productId);
      throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "The business does not exist");
    } catch (ProductNotFoundException exc) {
      log.error("PRODUCT NOT FOUND ERROR: " + productId);
      throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, exc.getMessage());
    } catch (ImageNotFoundException exc) {
      log.error("NO IMAGE RECEIVED");
      throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "NO IMAGE RECEIVED");
    } catch (BadCredentialsException exc) {
      log.error("INSUFFICIENT PRIVILEGES: " + productId);
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, exc.getMessage());
    } catch (Exception exc) {
      log.error("FAILED WHEN UPLOADING PRODUCT IMAGE" + exc.getMessage());
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unknown error");
    }
  }

  /**
   * Add image to business's product
   *
   * @param businessId id of the business
   * @param productId id of the product
   * @param imageId id of the image to set as primary image
   * @return The URI for the relative image location
   */
  @PutMapping("/businesses/{businessId}/products/{productId}/images/{imageId}/makeprimary")
  public ResponseEntity<String> changePrimaryImage(
      @PathVariable long businessId, @PathVariable long productId, @PathVariable long imageId) {
    try {
      imageService.changePrimaryImage(businessId, productId, imageId);
      log.info(
          "BUSINESS "
              + businessId
              + " SUCCESSFULLY UPDATED PRODUCT "
              + productId
              + "'s PRIMARY IMAGE TO IMAGE "
              + imageId);
      return new ResponseEntity<>(HttpStatus.CREATED);
    } catch (UserNotFoundException exc) {
      log.error("USER NOT FOUND ERROR: " + productId);
      throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "The user does not exist");
    } catch (BusinessNotFoundException exc) {
      log.error("BUSINESS NOT FOUND ERROR: " + productId);
      throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "The business does not exist");
    } catch (ProductNotFoundException exc) {
      log.error("PRODUCT NOT FOUND ERROR: " + productId);
      throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, exc.getMessage());
    } catch (BadCredentialsException exc) {
      log.error("INSUFFICIENT PRIVILEGES: " + productId);
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, exc.getMessage());
    } catch (Exception exc) {
      log.error("FAILED WHEN UPDATING PRIMARY PRODUCT IMAGE: " + exc);
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unknown error");
    }
  }

  @DeleteMapping("/businesses/{businessId}/products/{productId}/images/{imageId}")
  public ResponseEntity<Object> deleteProductImage(
      @PathVariable String businessId,
      @PathVariable String productId,
      @PathVariable String imageId) {
    try {
      this.imageService.deleteProductImage(
          Long.parseLong(imageId), Long.parseLong(businessId), Long.parseLong(productId));
      log.info("DELETED PRODUCT IMAGE - PRODUCT " + productId + " : IMAGE " + imageId);
      return new ResponseEntity<>(HttpStatus.valueOf(200));
    } catch (UserNotFoundException e) {
      log.error("NO USER LOGGED IN");
      throw new ResponseStatusException(HttpStatus.valueOf(406), "The user was not found");
    } catch (BusinessNotFoundException e) {
      log.error("BUSINESS NOT FOUND ERROR: " + businessId);
      throw new ResponseStatusException(HttpStatus.valueOf(406), "The business was not found");
    } catch (InsufficientPrivilegesException e) {
      log.error("INSUFFICIENT PRIVILEGES: " + businessId);
      throw new ResponseStatusException(
          HttpStatus.valueOf(403), "You are not admin/business admin");
    } catch (ProductNotFoundException e) {
      log.error("PRODUCT NOT FOUND ERROR: " + productId);
      throw new ResponseStatusException(HttpStatus.valueOf(406), "The product was not found");
    } catch (ImageNotFoundException e) {
      log.error("IMAGE NOT FOUND ERROR: " + imageId);
      throw new ResponseStatusException(HttpStatus.valueOf(406), "The image was not found");
    } catch (IOException e) {
      log.error("UNABLE TO UNLINK IMAGE FILE FROM FILE SYSTEM");
      throw new ResponseStatusException(HttpStatus.valueOf(500), "Could not delete image");
    }
  }
}
