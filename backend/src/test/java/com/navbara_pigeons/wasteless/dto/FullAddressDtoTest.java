package com.navbara_pigeons.wasteless.dto;

import com.navbara_pigeons.wasteless.testprovider.MainTestProvider;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class FullAddressDtoTest extends MainTestProvider {

  private Set<ConstraintViolation<FullAddressDto>> validate(FullAddressDto dto) {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    Validator validator = factory.getValidator();
    return validator.validate(dto);
  }

  @Test
  void validBusinessType() {
    Assertions.assertEquals(0, validate(new FullAddressDto(makeAddress())).size());
  }

  @Test
  void invalidFullAddressDtoWithLargeLengths() {
    FullAddressDto dto = new FullAddressDto(makeAddress());
    String string = "1";
    String badField = string.repeat(201);
    dto.setCity(badField);
    dto.setRegion(badField);
    dto.setCountry(badField);

    Assertions.assertEquals(3, validate(dto).size());
  }

  @Test
  void invalidFullAddressDtoWithNullFields() {
    FullAddressDto dto = new FullAddressDto(makeAddress());
    dto.setCountry(null);
    Assertions.assertEquals(1, validate(dto).size());
  }

  @Test
  void invalidFullAddressDtoWithBlankFields() {
    FullAddressDto dto = new FullAddressDto(makeAddress());
    dto.setCountry("");
    Assertions.assertEquals(1, validate(dto).size());
  }
}
