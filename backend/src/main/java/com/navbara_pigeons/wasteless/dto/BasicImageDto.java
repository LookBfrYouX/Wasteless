package com.navbara_pigeons.wasteless.dto;

import com.navbara_pigeons.wasteless.entity.Image;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;


/**
 * Image DTO which returns all image details
 */
@Data
public class BasicImageDto {

  private long id;

  @NotBlank(message = "Filename is Required")
  @Max(message = "Filename has to be less than or equal to 100 Characters", value = 100)
  private String filename;

  @NotBlank(message = "Thumbnail Filename is Required")
  @Max(message = "Thumbnail Filename has to be less than or equal to 100 Characters", value = 120)
  private String thumbnailFilename;

  public BasicImageDto(String publicPathPrefix, Image image) {
    this.id = image.getId();
    this.filename = publicPathPrefix + image.getPath().substring(1);
    System.out.println(this.filename);
    this.thumbnailFilename = publicPathPrefix + image.getThumbnailPath().substring(1);
  }

  public BasicImageDto() {

  }

}
