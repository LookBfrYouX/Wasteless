package com.navbara_pigeons.wasteless.dto;

import com.navbara_pigeons.wasteless.entity.Image;
import com.navbara_pigeons.wasteless.entity.Product;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Product DTO which returns all product information except `currency` and `primaryProductImage`
 * (returned as the first element in the image list)
 */
@Data
public class BasicProductDto {

  private long id;

  @NotBlank(message = "Name is Required")
  @Length(max=100, message = "Name has to be less than or equal to 100 Characters")
  private String name;

  @Length(max=50, message = "Description has to be less than or equal to 500 Characters")
  private String description;

  @Length(max=100, message = "Manufacturer has to be less than or equal to 100 Characters")
  private String manufacturer;

  @Min(message = "Recommended Retail Price must be Positive", value = 0)
  private Double recommendedRetailPrice;
  private ZonedDateTime created;
  private BasicImageDto primaryProductImage;
  private List<BasicImageDto> images;

  public BasicProductDto(Product product, String publicPathPrefix) {
    this.id = product.getId();
    this.name = product.getName();
    this.description = product.getDescription();
    this.manufacturer = product.getManufacturer();
    this.recommendedRetailPrice = product.getRecommendedRetailPrice();
    this.created = product.getCreated();
    if (product.getProductImages() != null) {
      this.images = makeImageDtos(publicPathPrefix,
          product.getImages()); // First image is primary image
    }
  }

  private List<BasicImageDto> makeImageDtos(String publicPathPrefix, List<Image> images) {
    ArrayList<BasicImageDto> imageDtos = new ArrayList<BasicImageDto>();
    for (Image image : images) {
      imageDtos.add(new BasicImageDto(publicPathPrefix, image));
    }
    return imageDtos;
  }

}
