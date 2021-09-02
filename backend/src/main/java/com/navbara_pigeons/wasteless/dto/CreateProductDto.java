package com.navbara_pigeons.wasteless.dto;

import com.navbara_pigeons.wasteless.entity.BusinessType;
import com.navbara_pigeons.wasteless.entity.Product;
import com.navbara_pigeons.wasteless.validation.constraints.StringEnumeration;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import jdk.jfr.BooleanFlag;
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

  @StringEnumeration(enumClass = NutriScore.class, message = "Invalid Nutri-Score given")
  private String nutriScore;

  @StringEnumeration(enumClass = NovaGroup.class, message = "Invalid Nova Group given")
  private String novaGroup;

  @StringEnumeration(enumClass = NutritionFactsLevel.class, message = "Invalid fat level given")
  private String fat;

  @StringEnumeration(enumClass = NutritionFactsLevel.class, message = "Invalid saturated fat level given")
  private String saturatedFat;

  @StringEnumeration(enumClass = NutritionFactsLevel.class, message = "Invalid sugar level given")
  private String sugar;

  @StringEnumeration(enumClass = NutritionFactsLevel.class, message = "Invalid salt level given")
  private String salt;

  @Nullable
  @Pattern(regexp = "^true$|^false$|^null$", message = "allowed input: true or false or null")
  private boolean isGlutenFree;

  @Nullable
  @Pattern(regexp = "^true$|^false$|^null$", message = "allowed input: true or false or null")
  private boolean isDairyFree;

  @Nullable
  @Pattern(regexp = "^true$|^false$|^null$", message = "allowed input: true or false or null")
  private boolean isVegetarian;

  @Nullable
  @Pattern(regexp = "^true$|^false$|^null$", message = "allowed input: true or false or null")
  private boolean isVegan;

  @Nullable
  @Pattern(regexp = "^true$|^false$|^null$", message = "allowed input: true or false or null")
  private boolean isPalmOilFree;

  @NotNull(message = "Business Type is Required")
  @Length(max = 50, message = "Business Type has to be less than or equal to 50 Characters")
  @StringEnumeration(enumClass = BusinessType.class, message = "Invalid business type given")
  private String businessType;


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
