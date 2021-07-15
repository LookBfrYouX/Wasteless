package com.navbara_pigeons.wasteless.dto;

import com.navbara_pigeons.wasteless.entity.Product;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

/**
 * Product DTO which contains only information required when creating a product
 */
@Data
public class BasicProductCreationDto {

  @NotBlank(message = "Name is Required")
  @Max(message = "Name has to be less than or equal to 100 Characters", value = 100)
  private String name;

  @Max(message = "Description has to be less than or equal to 500 Characters", value = 500)
  private String description;

  @Max(message = "Manufacturer has to be less than or equal to 100 Characters", value = 100)
  private String manufacturer;

  @Min(message = "Recommended Retail Price must be Positive", value = 0)
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
