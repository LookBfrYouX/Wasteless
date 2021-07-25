package com.navbara_pigeons.wasteless.dto;

import com.navbara_pigeons.wasteless.testprovider.MainTestProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

@SpringBootTest
public class CreateBusinessDtoTest extends MainTestProvider {
  private Set<ConstraintViolation<CreateBusinessDto>> validate(CreateBusinessDto dto) {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    Validator validator = factory.getValidator();
    Set<ConstraintViolation<CreateBusinessDto>> violations = validator.validate(dto);
    return violations;
  }

  @Test
  public void validBusinessType() {
    Assertions.assertEquals(0, validate(
            new CreateBusinessDto(makeBusiness()).setPrimaryAdministratorId(1L)
    ).size());
  }

  @Test
  public void invalidBusinessType() {
    CreateBusinessDto dto = new CreateBusinessDto(makeBusiness());
    dto.setBusinessType("Invalid business type");
    dto.setPrimaryAdministratorId(1L);
    Assertions.assertEquals(1, validate(dto).size());
  }
}
