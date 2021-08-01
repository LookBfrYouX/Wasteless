package com.navbara_pigeons.wasteless.dto;

import com.navbara_pigeons.wasteless.entity.Product;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import lombok.Data;

/**
 * Product DTO which contains only information required when creating a product
 */
@Data
public class BasicProductCreationDto {

  private String name;
  private String description;
  private String manufacturer;
  @DecimalMin(message = "RRP must be above 0.01", value = "0.01")
  @DecimalMax(message = "RRP must be below 10,000,000", value = "10000000.00")
  private Double recommendedRetailPrice;

  public BasicProductCreationDto() {

  }

  public BasicProductCreationDto(Product product) {
    this.name = product.getName();
    this.description = product.getDescription();
    this.manufacturer = product.getManufacturer();
    this.recommendedRetailPrice = product.getRecommendedRetailPrice();
  }

}
