package com.navbara_pigeons.wasteless.dto;

import com.navbara_pigeons.wasteless.testprovider.MainTestProvider;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.h2.command.ddl.CreateTable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CreateProductDtoTest extends MainTestProvider {

  private Set<ConstraintViolation<CreateProductDto>> validate(CreateProductDto dto) {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    Validator validator = factory.getValidator();
    Set<ConstraintViolation<CreateProductDto>> violations = validator.validate(dto);
    return violations;
  }

  @Test
  public void validBusinessType() {
    Assertions.assertEquals(0, validate(new CreateProductDto(makeProduct("product"))).size());
  }

  @Test
  public void invalidCreateProductDtoWithLargeLengths() {
    CreateProductDto dto = new CreateProductDto(makeProduct("product"));
    String string = "1";
    dto.setName(string.repeat(101));
    dto.setDescription(string.repeat(501));
    dto.setManufacturer(string.repeat(101));
    Assertions.assertEquals(3, validate(dto).size());
  }

  @Test
  public void invalidCreateProductDtoWithNegativeFields() {
    CreateProductDto dto = new CreateProductDto(makeProduct("product"));
    dto.setRecommendedRetailPrice(-1.0);
    Assertions.assertEquals(1, validate(dto).size());
  }

  @Test
  public void invalidCreateProductDtoWithBlankFields() {
    CreateProductDto dto = new CreateProductDto(makeProduct(""));
    Assertions.assertEquals(1, validate(dto).size());
  }

  @Test
  public void validCreateProductDtoWithNutriScore() {
    CreateProductDto dto = new CreateProductDto(makeProduct("test123"));
    dto.setNutriScore("A");
    Assertions.assertEquals("A", dto.getNutriScore());
  }

  @Test
  public void invalidCreateProductDtoWithInvalidNutriScore() {
    CreateProductDto dto = new CreateProductDto(makeProduct("test456"));
    dto.setNutriScore("Z");
    Assertions.assertEquals(1, validate(dto).size());
  }

  @Test
  public void validCreateProductDtoWithNovaGroup() {
    CreateProductDto dto = new CreateProductDto(makeProduct("test789"));
    dto.setNovaGroup("1");
    Assertions.assertEquals("1", dto.getNovaGroup());
  }

  @Test
  public void invalidCreateProductDtoWithInvalidNovaGroup() {
    CreateProductDto dto = new CreateProductDto(makeProduct("test987"));
    dto.setNovaGroup("5");
    Assertions.assertEquals(1, validate(dto).size());
  }

  @Test
  public void validCreateProductDtoWithFat() {
    CreateProductDto dto = new CreateProductDto(makeProduct("product123"));
    dto.setFat("LOW");
    Assertions.assertEquals("LOW", dto.getFat());
  }

  @Test
  public void invalidCreateProductDtoWithInvalidFat() {
    CreateProductDto dto = new CreateProductDto(makeProduct("product456"));
    dto.setFat("BLA");
    Assertions.assertEquals(1, validate(dto).size());
  }

  @Test
  public void validCreateProductDtoWithSaturatedFat() {
    CreateProductDto dto = new CreateProductDto(makeProduct("product789"));
    dto.setSaturatedFat("MODERATE");
    Assertions.assertEquals("MODERATE", dto.getSaturatedFat());
  }

  @Test
  public void invalidCreateProductDtoWithInvalidSaturatedFat() {
    CreateProductDto dto = new CreateProductDto(makeProduct("product987"));
    dto.setSaturatedFat("BLA");
    Assertions.assertEquals(1, validate(dto).size());
  }

  @Test
  public void validCreateProductDtoWithSugar() {
    CreateProductDto dto = new CreateProductDto(makeProduct("product654"));
    dto.setSugar("HIGH");
    Assertions.assertEquals("HIGH", dto.getSugar());
  }

  @Test
  public void invalidCreateProductDtoWithInvalidSugar() {
    CreateProductDto dto = new CreateProductDto(makeProduct("product321"));
    dto.setSugar("BLA");
    Assertions.assertEquals(1, validate(dto).size());
  }

  @Test
  public void validCreateProductDtoWithSalt() {
    CreateProductDto dto = new CreateProductDto(makeProduct("product654"));
    dto.setSalt("LOW");
    Assertions.assertEquals("LOW", dto.getSalt());
  }

  @Test
  public void invalidCreateProductDtoWithInvalidSalt() {
    CreateProductDto dto = new CreateProductDto(makeProduct("example123"));
    dto.setSalt("BLA");
    Assertions.assertEquals(1, validate(dto).size());
  }

  @Test
  public void validCreateProductDtoWithIsGlutenFree() {
    CreateProductDto dto = new CreateProductDto(makeProduct("example456"));
    dto.setGlutenFree(true);
    Assertions.assertEquals(true, dto.isGlutenFree());
  }

  @Test
  public void validCreateProductDtoWithNullIsGlutenFree() {
    CreateProductDto dto = new CreateProductDto(makeProduct("example789"));
    Assertions.assertEquals(null, dto.isGlutenFree());
  }

  @Test
  public void invalidCreateProductDtoWithInvalidIsGlutenFree() {
    CreateProductDto dto = new CreateProductDto(makeProduct("example789"));
    dto.setDairyFree("BLA");
    Assertions.assertEquals(1, validate(dto).size());
  }

  @Test
  public void validCreateProductDtoWithIsDairyFree() {
    CreateProductDto dto = new CreateProductDto(makeProduct("example456"));
    dto.setDairyFree(true);
    Assertions.assertEquals(true, dto.isDairyFree());
  }

  @Test
  public void validCreateProductDtoWithNullIsDairyFree() {
    CreateProductDto dto = new CreateProductDto(makeProduct("example789"));
    Assertions.assertEquals(null, dto.isGlutenFree());
  }

  @Test
  public void invalidCreateProductDtoWithInvalidIsGlutenFree() {
    CreateProductDto dto = new CreateProductDto(makeProduct("example789"));
    dto.setDairyFree("BLA");
    Assertions.assertEquals(1, validate(dto).size());
  }

  @Test
  public void validCreateProductDtoWithIsVegetarian() {
    CreateProductDto dto = new CreateProductDto(makeProduct("example456"));
    dto.setVegetarian(true);
    Assertions.assertEquals(true, dto.isVegetarian());
  }

  @Test
  public void validCreateProductDtoWithNullIsVegetarian() {
    CreateProductDto dto = new CreateProductDto(makeProduct("example789"));
    Assertions.assertEquals(null, dto.isVegetarian());
  }

  @Test
  public void invalidCreateProductDtoWithInvalidIsVegetarian() {
    CreateProductDto dto = new CreateProductDto(makeProduct("example789"));
    dto.setVegetarian("BLA");
    Assertions.assertEquals(1, validate(dto).size());
  }

  @Test
  public void validCreateProductDtoWithIsVegan() {
    CreateProductDto dto = new CreateProductDto(makeProduct("example456"));
    dto.setVegetarian(false);
    Assertions.assertEquals(false, dto.isVegetarian());
  }

  @Test
  public void validCreateProductDtoWithNullIsVegan() {
    CreateProductDto dto = new CreateProductDto(makeProduct("example789"));
    Assertions.assertEquals(null, dto.isVegetarian());
  }

  @Test
  public void invalidCreateProductDtoWithInvalidIsVegan() {
    CreateProductDto dto = new CreateProductDto(makeProduct("example789"));
    dto.setVegetarian("BLA");
    Assertions.assertEquals(1, validate(dto).size());
  }

  @Test
  public void validCreateProductDtoWithIsPalmOilFree() {
    CreateProductDto dto = new CreateProductDto(makeProduct("example456"));
    dto.setPalmOilFree(false);
    Assertions.assertEquals(false, dto.isPalmOilFree());
  }

  @Test
  public void validCreateProductDtoWithNullIsPalmOilFree() {
    CreateProductDto dto = new CreateProductDto(makeProduct("example789"));
    Assertions.assertEquals(null, dto.isPalmOilFree());
  }

  @Test
  public void invalidCreateProductDtoWithInvalidIsVegan() {
    CreateProductDto dto = new CreateProductDto(makeProduct("example789"));
    dto.setPalmOilFree("BLA");
    Assertions.assertEquals(1, validate(dto).size());
  }
}
