package com.navbara_pigeons.wasteless.dto;

import com.navbara_pigeons.wasteless.entity.Product;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * Product DTO which contains only information required when creating a product
 */
@Data
public class CreateProductDto {

  @NotBlank(message = "Name is Required")
  @Length(max = 100, message = "Name has to be less than or equal to 100 Characters")
  private String name;

  @Length(max = 500, message = "Description has to be less than or equal to 500 Characters")
  private String description;

  @Length(max = 100, message = "Manufacturer has to be less than or equal to 100 Characters")
  private String manufacturer;

  @DecimalMin(message = "RRP must be above 0.01", value = "0.01")
  @DecimalMax(message = "RRP must be below 10,000,000", value = "10000000.00")
  private Double recommendedRetailPrice;


  public CreateProductDto() {

  }

  public CreateProductDto(Product product) {
    this.name = product.getName();
    this.description = product.getDescription();
    this.manufacturer = product.getManufacturer();
    this.recommendedRetailPrice = product.getRecommendedRetailPrice();
  }

}
