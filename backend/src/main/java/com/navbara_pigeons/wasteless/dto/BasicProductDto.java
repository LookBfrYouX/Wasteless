package com.navbara_pigeons.wasteless.dto;

import com.navbara_pigeons.wasteless.entity.Image;
import com.navbara_pigeons.wasteless.entity.Product;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import lombok.Data;

/**
 * Product DTO which returns all product information except `currency` and `primaryProductImage`
 * (returned as the first element in the image list)
 */
@Data
public class BasicProductDto {

  private long id;
  private String name;
  private String description;
  private String manufacturer;
  @DecimalMin(message="RRP must be above 0.01", value="0.01")
  @DecimalMax(message="RRP must be below 10,000,000", value="10000000.00")
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
