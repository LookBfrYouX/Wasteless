package com.navbara_pigeons.wasteless.dto;

import com.navbara_pigeons.wasteless.entity.Product;
import com.navbara_pigeons.wasteless.enums.NutriScore;
import com.navbara_pigeons.wasteless.enums.NutritionFactsLevel;
import com.navbara_pigeons.wasteless.testprovider.MainTestProvider;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class CreateProductDtoTest extends MainTestProvider {

  private Set<ConstraintViolation<CreateProductDto>> validate(CreateProductDto dto) {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    Validator validator = factory.getValidator();
    Set<ConstraintViolation<CreateProductDto>> violations = validator.validate(dto);
    return violations;
  }

  @Test
  void validBusinessType() {
    Assertions.assertEquals(0, validate(new CreateProductDto(makeProduct("product"))).size());
  }

  @Test
  void invalidCreateProductDtoWithLargeLengths() {
    CreateProductDto dto = new CreateProductDto(makeProduct("product"));
    String string = "1";
    dto.setName(string.repeat(101));
    dto.setDescription(string.repeat(501));
    dto.setManufacturer(string.repeat(101));
    Assertions.assertEquals(3, validate(dto).size());
  }

  @Test
  void invalidCreateProductDtoWithNegativeFields() {
    CreateProductDto dto = new CreateProductDto(makeProduct("product"));
    dto.setRecommendedRetailPrice(-1.0);
    Assertions.assertEquals(1, validate(dto).size());
  }

  @Test
  void invalidCreateProductDtoWithBlankFields() {
    CreateProductDto dto = new CreateProductDto(makeProduct(""));
    Assertions.assertEquals(1, validate(dto).size());
  }

  @Test
  void createProductDto_withNutriScore_expectOk() {
    CreateProductDto dto = new CreateProductDto(makeProduct("test123"));
    dto.setNutriScore(NutriScore.E);
    Assertions.assertEquals("E", dto.getNutriScore().name());
  }

  @Test
  void createProductDto_withNovaGroup_expectOk() {
    CreateProductDto dto = new CreateProductDto(makeProduct("test789"));
    dto.setNovaGroup(1);
    Assertions.assertEquals(1, dto.getNovaGroup());
  }

  @Test
  void createProductDto_withFat_expectOk() {
    CreateProductDto dto = new CreateProductDto(makeProduct("product123"));
    dto.setFat(NutritionFactsLevel.LOW);
    Assertions.assertEquals("LOW", dto.getFat().name());
  }

  @Test
  void createProductDto_withSaturatedFat_expectOk() {
    CreateProductDto dto = new CreateProductDto(makeProduct("product789"));
    dto.setSaturatedFat(NutritionFactsLevel.MODERATE);
    Assertions.assertEquals("MODERATE", dto.getSaturatedFat().name());
  }

  @Test
  void createProductDto_withSugars_expectOk() {
    CreateProductDto dto = new CreateProductDto(makeProduct("product654"));
    dto.setSugars(NutritionFactsLevel.HIGH);
    Assertions.assertEquals("HIGH", dto.getSugars().name());
  }

  @Test
  void createProductDto_withSalt_expectOk() {
    CreateProductDto dto = new CreateProductDto(makeProduct("product654"));
    dto.setSalt(NutritionFactsLevel.LOW);
    Assertions.assertEquals("LOW", dto.getSalt().name());
  }

  @Test
  void createProductDto_withIsGlutenFree_expectOk() {
    CreateProductDto dto = new CreateProductDto(makeProduct("example456"));
    dto.setIsGlutenFree(true);
    Assertions.assertEquals(true, dto.getIsGlutenFree());
  }

  @Test
  void createProductDto_withNullIsGlutenFree_expectOk() {
    CreateProductDto dto = new CreateProductDto(makeProduct("example789"));
    Assertions.assertEquals(null, dto.getIsGlutenFree());
  }

  @Test
  void createProductDto_withIsDairyFree_expectOk() {
    CreateProductDto dto = new CreateProductDto(makeProduct("example456"));
    dto.setIsDairyFree(true);
    Assertions.assertEquals(true, dto.getIsDairyFree());
  }

  @Test
  void createProductDto_withNullIsDairyFree_expectOk() {
    CreateProductDto dto = new CreateProductDto(makeProduct("example789"));
    Assertions.assertEquals(null, dto.getIsDairyFree());
  }

  @Test
  void createProductDto_withIsVegetarian_expectOk() {
    CreateProductDto dto = new CreateProductDto(makeProduct("example456"));
    dto.setIsVegetarian(true);
    Assertions.assertEquals(true, dto.getIsVegetarian());
  }

  @Test
  void createProductDto_withNullIsVegetarian_expectOk() {
    CreateProductDto dto = new CreateProductDto(makeProduct("example789"));
    Assertions.assertEquals(null, dto.getIsVegetarian());
  }

  @Test
  void createProductDto_withIsVegan_expectOk() {
    CreateProductDto dto = new CreateProductDto(makeProduct("example456"));
    dto.setIsVegan(false);
    Assertions.assertEquals(false, dto.getIsVegan());
  }

  @Test
  void createProductDto_withNullIsVegan_expectOk() {
    CreateProductDto dto = new CreateProductDto(makeProduct("example789"));
    Assertions.assertEquals(null, dto.getIsVegan());
  }

  @Test
  void createProductDto_withIsPalmOilFree_expectOk() {
    CreateProductDto dto = new CreateProductDto(makeProduct("example456"));
    dto.setIsPalmOilFree(false);
    Assertions.assertEquals(false, dto.getIsPalmOilFree());
  }

  @Test
  void createProductDto_withNullIsPalmOilFree_expectOk() {
    CreateProductDto dto = new CreateProductDto(makeProduct("example789"));
    Assertions.assertEquals(null, dto.getIsPalmOilFree());
  }

  @Test
  void mapProductToDto_withValidNutriscore_expectOk() {
    Product tempProduct = makeProduct("Test Product 01");
    tempProduct.setNutriScore(NutriScore.B);
    BasicProductDto productDto = new BasicProductDto(tempProduct, null);
    Assertions.assertEquals("B", productDto.getNutriScore().name());
  }

  @Test
  void mapProductToDto_withNullNutriscore_expectOk() {
    Product tempProduct = makeProduct("Test Product 02");
    tempProduct.setNutriScore(null);
    Assertions.assertDoesNotThrow(() -> { BasicProductDto productDto = new BasicProductDto(tempProduct, null); });
  }

  @Test
  void mapProductToDto_withValidNovascore_expectOk() {
    Product tempProduct = makeProduct("Test Product 03");
    tempProduct.setNovaGroup(4);
    BasicProductDto productDto = new BasicProductDto(tempProduct, null);
    Assertions.assertEquals(4, productDto.getNovaScore());
  }

  @Test
  void mapProductToDto_withNullNovascore_expectOk() {
    Product tempProduct = makeProduct("Test Product 04");
    tempProduct.setNovaGroup(null);
    Assertions.assertDoesNotThrow(() -> { BasicProductDto productDto = new BasicProductDto(tempProduct, null); });
  }

}
