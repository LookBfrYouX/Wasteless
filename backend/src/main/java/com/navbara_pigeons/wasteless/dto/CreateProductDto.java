package com.navbara_pigeons.wasteless.dto;

import com.navbara_pigeons.wasteless.entity.Product;
import com.navbara_pigeons.wasteless.enums.NutriScore;
import com.navbara_pigeons.wasteless.enums.NutritionFactsLevel;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.lang.Nullable;

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

  private NutriScore nutriScore;

  @Min(1)
  @Max(4)
  private Integer novaGroup;

  private NutritionFactsLevel fat;

  private NutritionFactsLevel saturatedFat;

  private NutritionFactsLevel sugar;

  private NutritionFactsLevel salt;

  @Nullable
  private Boolean isGlutenFree;

  @Nullable
  private Boolean isDairyFree;

  @Nullable
  private Boolean isVegetarian;

  @Nullable
  private Boolean isVegan;

  @Nullable
  private Boolean isPalmOilFree;

  public CreateProductDto() {

  }

  public CreateProductDto(Product product) {
    this.name = product.getName();
    this.description = product.getDescription();
    this.manufacturer = product.getManufacturer();
    this.recommendedRetailPrice = product.getRecommendedRetailPrice();
    this.nutriScore = product.getNutriScore();
    this.novaGroup = product.getNovaGroup();
    this.fat = product.getFat();
    this.saturatedFat = product.getSaturatedFat();
    this.sugar = product.getSugar();
    this.salt = product.getSalt();
    this.isGlutenFree = product.getIsGlutenFree();
    this.isDairyFree = product.getIsDairyFree();
    this.isVegetarian = product.getIsVegetarian();
    this.isVegan = product.getIsVegan();
    this.isPalmOilFree = product.getIsPalmOilFree();
  }

}
