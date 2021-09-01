package com.navbara_pigeons.wasteless.dto;

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
    dto.setNovaGroup("AD");
    dto.setNutriScore("5");
    Assertions.assertEquals(5, validate(dto).size());
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
}
