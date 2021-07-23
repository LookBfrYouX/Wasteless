package com.navbara_pigeons.wasteless.dto;

import com.navbara_pigeons.wasteless.testprovider.MainTestProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

public class BasicUserDtoTest extends MainTestProvider {
    private Set<ConstraintViolation<BasicUserDto>> validate(BasicUserDto dto) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<BasicUserDto>> violations = validator.validate(dto);
        return violations;
    }

    @Test
    public void validBusinessType() {
        Assertions.assertEquals(0, validate(new BasicUserDto(makeUser("email@email.co.nz","password123",false))).size());
    }

    @Test
    public void invalidBasicUserDtoWithLargeLengths() {
        BasicUserDto dto = new BasicUserDto(makeUser("email@email.co.nz","password123",false));
        String string = "1";
        String badField = string.repeat(51);
        dto.setFirstName(badField);
        dto.setLastName(badField);
        dto.setMiddleName(badField);
        dto.setNickname(badField);
        dto.setBio(string.repeat(251));
        Assertions.assertEquals(5, validate(dto).size());
    }

    @Test
    public void invalidBasicUserDtoWithBlankFields() {
        BasicUserDto dto = new BasicUserDto(makeUser("email@email.co.nz","password123",false));
        dto.setFirstName("");
        dto.setLastName("");
        Assertions.assertEquals(2, validate(dto).size());
    }
}
