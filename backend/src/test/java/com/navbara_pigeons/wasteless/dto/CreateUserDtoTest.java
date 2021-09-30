package com.navbara_pigeons.wasteless.dto;

import com.navbara_pigeons.wasteless.testprovider.MainTestProvider;
import java.time.LocalDate;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class CreateUserDtoTest extends MainTestProvider {

  private Set<ConstraintViolation<CreateUserDto>> validate(CreateUserDto dto) {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    Validator validator = factory.getValidator();
    return validator.validate(dto);
  }

  @Test
  void validBusinessType() {
    Assertions.assertEquals(0,
        validate(new CreateUserDto(makeUser("email@email.co.nz", "password123", false))).size());
  }

  @Test
  void invalidCreateUserDtoWithLargeLengths() {
    CreateUserDto dto = new CreateUserDto(makeUser("email@email.co.nz", "password123", false));
    String string = "1";
    String badField = string.repeat(51);
    dto.setFirstName(badField);
    dto.setLastName(badField);
    dto.setMiddleName(badField);
    dto.setNickname(badField);
    dto.setBio(string.repeat(251));
    dto.setEmail(badField + "@email.com");
    dto.setPhoneNumber(string.repeat(26));
    dto.setPassword(string.repeat(60) + "aB");
    Assertions.assertEquals(8, validate(dto).size());
  }

  @Test
  void invalidCreateUserDtoWithBlankFields() {
    CreateUserDto dto = new CreateUserDto(makeUser("email@email.co.nz", "password123", false));
    dto.setFirstName("");
    dto.setLastName("");
    dto.setPassword("");
    Assertions.assertEquals(4, validate(dto).size());
  }

  @Test
  void invalidCreateUserDtoWithNullFields() {
    CreateUserDto dto = new CreateUserDto(makeUser("email@email.co.nz", "password123", false));
    dto.setDateOfBirth(null);
    Assertions.assertEquals(1, validate(dto).size());
  }

  @Test
  void invalidCreateUserFutureDateOfBirth() {
    CreateUserDto dto = new CreateUserDto(makeUser("email@email.co.nz", "password123", false));
    dto.setDateOfBirth(LocalDate.now().plusYears(10));
    Assertions.assertEquals(1, validate(dto).size());
  }

  @Test
  void invalidCreateUserDateOfBirthTooOld() {
    CreateUserDto dto = new CreateUserDto(makeUser("email@email.co.nz", "password123", false));
    dto.setDateOfBirth(LocalDate.now().minusYears(150));
    Assertions.assertEquals(1, validate(dto).size());
  }

  @Test
  void validCreateUserDateOfBirthMinimumAge() {
    CreateUserDto dto = new CreateUserDto(makeUser("email@email.co.nz", "password123", false));
    dto.setDateOfBirth(LocalDate.now().minusYears(13));
    Assertions.assertEquals(0, validate(dto).size());
  }

}
