package com.navbara_pigeons.wasteless.dto;

import com.navbara_pigeons.wasteless.entity.Keyword;
import com.navbara_pigeons.wasteless.testprovider.MainTestProvider;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class CreateKeywordDtoTest extends MainTestProvider {

  private Set<ConstraintViolation<CreateKeywordDto>> validate(CreateKeywordDto dto) {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    Validator validator = factory.getValidator();
    return validator.validate(dto);
  }

  @Test
  void validKeywordType() {
    Keyword keyword = makeKeyword();
    Assertions.assertEquals(0, validate(new CreateKeywordDto(keyword)).size());
  }

  @Test
  void invalidCreateKeywordDtoWithLargeLengths() {
    Keyword keyword = makeKeyword();
    keyword.setName("Toooooooooooooooooooooooooooooooooooooooooooooooooo Long Keyword");
    Assertions.assertEquals(1, validate(new CreateKeywordDto(keyword)).size());
  }

  @Test
  void invalidCreateKeywordDtoWithNullFields() {
    Keyword keyword = makeKeyword();
    keyword.setName(null);
    Assertions.assertEquals(1, validate(new CreateKeywordDto(keyword)).size());
  }
}
