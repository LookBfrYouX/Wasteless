package com.navbara_pigeons.wasteless.dto;

import com.navbara_pigeons.wasteless.entity.Image;
import com.navbara_pigeons.wasteless.entity.Product;
import com.navbara_pigeons.wasteless.enums.NutriScore;
import com.navbara_pigeons.wasteless.enums.NutritionFactsLevel;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
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
  private Double recommendedRetailPrice;
  private ZonedDateTime created;
  private List<BasicImageDto> images;
  private NutriScore nutriScore;
  private Integer novaScore;
  private NutritionFactsLevel fat;
  private NutritionFactsLevel saturatedFat;
  private NutritionFactsLevel sugars;
  private NutritionFactsLevel salt;
  private Boolean isGlutenFree;
  private Boolean isDairyFree;
  private Boolean isVegetarian;
  private Boolean isVegan;
  private Boolean isPalmOilFree;

  /**
   * This constructor maps a Product entity to the BasicProductDTO.
   *
   * @param product The product to be mapped.
   * @param publicPathPrefix The public image path prefix
   */
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
    this.nutriScore = product.getNutriScore();
    this.novaScore = product.getNovaGroup();
    this.fat = product.getFat();
    this.saturatedFat = product.getSaturatedFat();
    this.sugars = product.getSugars();
    this.salt = product.getSalt();
    this.isGlutenFree = product.getIsGlutenFree();
    this.isDairyFree = product.getIsDairyFree();
    this.isVegetarian = product.getIsVegetarian();
    this.isVegan = product.getIsVegan();
    this.isPalmOilFree = product.getIsPalmOilFree();
  }

  private List<BasicImageDto> makeImageDtos(String publicPathPrefix, List<Image> images) {
    ArrayList<BasicImageDto> imageDtos = new ArrayList<>();
    for (Image image : images) {
      imageDtos.add(new BasicImageDto(publicPathPrefix, image));
    }
    return imageDtos;
  }
}
