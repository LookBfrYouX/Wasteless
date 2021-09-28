package com.navbara_pigeons.wasteless.dto;

import com.navbara_pigeons.wasteless.testprovider.MainTestProvider;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CreateBusinessDtoTest extends MainTestProvider {

  private Set<ConstraintViolation<CreateBusinessDto>> validate(CreateBusinessDto dto) {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    Validator validator = factory.getValidator();
    Set<ConstraintViolation<CreateBusinessDto>> violations = validator.validate(dto);
    return violations;
  }

  @Test
  void validBusinessType() {
    Assertions.assertEquals(0, validate(new CreateBusinessDto(makeBusiness())).size());
  }

  @Test
  void invalidCreateBusinessDtoWithLargeLengths() {
    CreateBusinessDto dto = new CreateBusinessDto(makeBusiness());
    String string = "1";
    String badDescription = string.repeat(451);
    String badName = string.repeat(51);
    dto.setDescription(badDescription);
    dto.setName(badName);

    Assertions.assertEquals(2, validate(dto).size());
  }

  @Test
  void invalidCreateBusinessDtoWithNullFields() {
    CreateBusinessDto dto = new CreateBusinessDto(makeBusiness());
    dto.setName(null);
    dto.setBusinessType(null);
    Assertions.assertEquals(2, validate(dto).size());
  }
}
