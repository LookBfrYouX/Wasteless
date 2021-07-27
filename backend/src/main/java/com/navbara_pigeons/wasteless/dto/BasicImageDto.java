package com.navbara_pigeons.wasteless.dto;

import com.navbara_pigeons.wasteless.entity.Image;
import lombok.Data;


/**
 * Image DTO which returns all image details
 */
@Data
public class BasicImageDto {

  private long id;
  private String filename;
  private String thumbnailFilename;

  /**
   * Constructor for creating Image DTO from Image entity. (needs a public path prefix)
   * @param publicPathPrefix
   * @param image
   */
  public BasicImageDto(String publicPathPrefix, Image image) {
    this.id = image.getId();
    this.filename = publicPathPrefix + image.getPath().substring(1);
    this.thumbnailFilename = publicPathPrefix + image.getThumbnailPath().substring(1);
  }

  public BasicImageDto() {

  }

}
