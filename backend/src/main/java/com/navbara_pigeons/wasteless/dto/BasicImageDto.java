package com.navbara_pigeons.wasteless.dto;

import com.navbara_pigeons.wasteless.entity.Image;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;


/**
 * Image DTO which returns all image details
 */
@Data
public class BasicImageDto {

  private long id;

  @NotBlank(message = "Filename is Required")
  @Length(max=100, message = "Filename has to be less than or equal to 100 Characters")
  private String filename;

  @NotBlank(message = "Thumbnail Filename is Required")
  @Length(max=120, message = "Description has to be less than or equal to 120 Characters")
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
