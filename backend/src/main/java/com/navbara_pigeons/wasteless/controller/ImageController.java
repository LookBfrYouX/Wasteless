package com.navbara_pigeons.wasteless.controller;

import com.navbara_pigeons.wasteless.exception.*;
import com.navbara_pigeons.wasteless.service.ImageService;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

/**
 * @author Alec Fox, Fletcher Dick, Haruka Ichinose
 */
@Controller
@Slf4j
public class ImageController {

  private final ImageService imageService;

  /**
   * The Constructor fot the imageContrller class
   * @param imageService The Image service to be linked
   */
  public ImageController(@Autowired ImageService imageService) {
    this.imageService = imageService;
  }

  /**
   * Add image to business's product
   *
   * @param businessId id of the business
   * @param productId  id of the product
   * @param image      image to be added
   * @return The URI for the relative image location
   */
  @PostMapping("/businesses/{businessId}/products/{productId}/images")
  public ResponseEntity<String> uploadProductImage(@PathVariable long businessId,
      @PathVariable long productId, @RequestParam MultipartFile image) {
    try {
      String response = imageService.uploadProductImage(businessId, productId, image);
      log.info(
          "PRODUCT " + productId + " SUCCESSFULLY UPLOADED IMAGE " + image.getOriginalFilename() +
          " TO BUSINESS " + businessId);
      return new ResponseEntity<>(response, HttpStatus.CREATED);
    } catch (UserNotFoundException exc) {
      log.error("USER NOT FOUND ERROR: " + productId);
      throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "The user does not exist");
    }catch (BusinessNotFoundException exc) {
      log.error("BUSINESS NOT FOUND ERROR: " + productId);
      throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "The business does not exist");
    }catch (ProductNotFoundException exc) {
      log.error("PRODUCT NOT FOUND ERROR: " + productId);
      throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, exc.getMessage());
    } catch (BadCredentialsException exc) {
      log.error("INSUFFICIENT PRIVILEGES: " + productId);
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, exc.getMessage());
    } catch (Exception exc) {
      log.error("FAILED WHEN UPLOADING PRODUCT IMAGE");
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unknown error");
    }
  }

  /**
   * Add image to business's product
   *
   * @param businessId id of the business
   * @param productId  id of the product
   * @param imageId    id of the image to set as primary image
   * @return The URI for the relative image location
   */
  @PutMapping("/businesses/{businessId}/products/{productId}/images/{imageId}/makeprimary")
  public ResponseEntity<String> changePrimaryImage(@PathVariable long businessId,
      @PathVariable long productId, @PathVariable long imageId) {
    try {
      imageService.changePrimaryImage(businessId, productId, imageId);
      log.info(
          "BUSINESS " + businessId + " SUCCESSFULLY UPDATED PRODUCT " + productId +
              "'s PRIMARY IMAGE TO " + imageId);
      return new ResponseEntity<>(HttpStatus.CREATED);
    } catch (UserNotFoundException exc) {
      log.error("USER NOT FOUND ERROR: " + productId);
      throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "The user does not exist");
    }catch (BusinessNotFoundException exc) {
      log.error("BUSINESS NOT FOUND ERROR: " + productId);
      throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "The business does not exist");
    }catch (ProductNotFoundException exc) {
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

  /**
   * Add image to users profile
   *
   * @param id id of the user
   * @param image image to be added
   * @return url of the uploaded image
   */
  @PostMapping("/users/{id}/images")
  public ResponseEntity<String> uploadProfileImage(@PathVariable long id,
      @RequestParam MultipartFile image) {
    try {
      String response = imageService.uploadProfileImage(id, image);
      log.info(
          "USER " + id + " SUCCESSFULLY UPLOADED PROFILE IMAGE " + image.getOriginalFilename());
      return new ResponseEntity<>(response, HttpStatus.CREATED);
    } catch (UserNotFoundException exc) {
      log.error("USER NOT FOUND ERROR: " + id);
      throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "The user does not exist");
    } catch (BadCredentialsException exc) {
      log.error("INSUFFICIENT PRIVILEGES: " + id);
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, exc.getMessage());
    } catch (Exception exc) {
      log.error("FAILED WHEN UPLOADING PROFILE IMAGE");
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unknown error");
    }
  }

  /**
   * Download the image for the specified user.
   *
   * @param id The identifier of the user
   * @return The byte array of the users profile image
   */
  @GetMapping(
      value = "/users/{id}/images",
      produces = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_GIF_VALUE, MediaType.IMAGE_PNG_VALUE}
  )
  public @ResponseBody
  ResponseEntity<byte[]> downloadProfileImage(@PathVariable long id) {
    try {
      byte[] response = imageService.downloadProfileImage(id);
      log.info("SUCCESSFULLY DOWNLOADED USER " + id + "'s PROFILE IMAGE");
      return new ResponseEntity<>(response, HttpStatus.OK);
    } catch (UserNotFoundException exc) {
      log.error("USER NOT FOUND ERROR: " + id);
      throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "The user does not exist");
    } catch (IOException exc) {
      log.error("USER DOES NOT HAVE AN IMAGE");
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, exc.getMessage());
    } catch (Exception exc) {
      log.error("FAILED WHEN DOWNLOADING PROFILE IMAGE");
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unknown error");
    }
  }

  @DeleteMapping("/businesses/{businessId}/products/{productId}/images/{imageId}")
  public ResponseEntity deleteProductImage(@PathVariable String businessId, @PathVariable String productId, @PathVariable String imageId) {
    // TODO
    try {
      this.imageService.deleteProductImage(Long.parseLong(imageId), Long.parseLong(businessId), Long.parseLong(productId));
      log.info("DELETED PRODUCT IMAGE - PRODUCT " + productId + " : IMAGE " + imageId);
      return new ResponseEntity(HttpStatus.valueOf(200));
    } catch (UserNotFoundException e) {
      e.printStackTrace();
      throw new ResponseStatusException(HttpStatus.valueOf(406));
    } catch (BusinessNotFoundException e) {
      e.printStackTrace();
      throw new ResponseStatusException(HttpStatus.valueOf(406));
    } catch (InsufficientPrivilegesException e) {
      e.printStackTrace();
      throw new ResponseStatusException(HttpStatus.valueOf(403));
    } catch (ProductNotFoundException e) {
      e.printStackTrace();
      throw new ResponseStatusException(HttpStatus.valueOf(406));
    } catch (ImageNotFoundException e) {
      e.printStackTrace();
      throw new ResponseStatusException(HttpStatus.valueOf(406));
    } catch (IOException e) {
      e.printStackTrace();
      throw new ResponseStatusException(HttpStatus.valueOf(500));
    }
  }

}
