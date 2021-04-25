package com.navbara_pigeons.wasteless.controller;

import com.navbara_pigeons.wasteless.service.ImageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Fletcher Dick, Haruka Ichinose
 */
@Controller
@Slf4j
@RequestMapping("/images")
public class ImageController {

  private final ImageService imageService;

  public ImageController(@Autowired ImageService imageService) {
    this.imageService = imageService;
  }

  @PostMapping("/user/upload")
  public ResponseEntity<String> uploadProfileImage(@RequestParam MultipartFile image) {
    return imageService.uploadProfileImage(image);
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
