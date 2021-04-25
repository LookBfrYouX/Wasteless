package com.navbara_pigeons.wasteless.controller;

import com.navbara_pigeons.wasteless.exception.UserNotFoundException;
import com.navbara_pigeons.wasteless.service.ImageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

/**
 * @author Fletcher Dick, Haruka Ichinose
 */
@Controller
@Slf4j
public class ImageController {

  private final ImageService imageService;

  public ImageController(@Autowired ImageService imageService) {
    this.imageService = imageService;
  }

  @PostMapping("/users/{id}/images")
  public ResponseEntity<String> uploadProfileImage(@PathVariable long id,
      @RequestParam MultipartFile image) {
    try {
      String response = imageService.uploadProfileImage(id, image);
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

//  @GetMapping(
//      value = "getImage/{imageName:.+}",
//      produces = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_GIF_VALUE, MediaType.IMAGE_PNG_VALUE}
//  )
//  public @ResponseBody
//  byte[] getImageWithMediaType(@PathVariable(name = "imageName") String fileName)
//      throws IOException {
//    return imageService.getImageWithMediaType(fileName);
//  }

}
