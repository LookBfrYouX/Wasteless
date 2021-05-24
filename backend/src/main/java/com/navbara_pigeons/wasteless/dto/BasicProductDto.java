package com.navbara_pigeons.wasteless.dto;

import com.navbara_pigeons.wasteless.entity.Image;
import com.navbara_pigeons.wasteless.entity.Product;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;

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
  private Double recommendedRetailPrice;
  private ZonedDateTime created;
  private BasicImageDto primaryProductImage;
  private List<BasicImageDto> images;
  @Value("${public_path_prefix}")
  private String publicPathPrefix;

  public BasicProductDto(Product product) {
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
