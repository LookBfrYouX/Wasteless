package com.navbara_pigeons.wasteless.dto;

import com.navbara_pigeons.wasteless.entity.Product;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Product DTO which contains only information required when creating a product
 */
@Data
public class BasicProductCreationDto {

  @NotBlank(message = "Name is Required")
  @NotNull(message = "Name cannot be Null")
  @Length(max=100, message = "Name has to be less than or equal to 100 Characters")
  private String name;

  @Length(max=500, message = "Description has to be less than or equal to 500 Characters")
  private String description;

  @Length(max=100, message = "Manufacturer has to be less than or equal to 100 Characters")
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
