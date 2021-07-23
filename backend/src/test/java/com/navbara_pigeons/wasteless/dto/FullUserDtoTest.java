package com.navbara_pigeons.wasteless.dto;

import com.navbara_pigeons.wasteless.testprovider.MainTestProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

public class FullUserDtoTest extends MainTestProvider {
    private Set<ConstraintViolation<FullUserDto>> validate(FullUserDto dto) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<FullUserDto>> violations = validator.validate(dto);
        return violations;
    }

    @Test
    public void validBusinessType() {
        Assertions.assertEquals(0, validate(new FullUserDto(makeUser("email@email.co.nz","password123",false))).size());
    }

    @Test
    public void invalidFullUserDtoWithLargeLengths() {
        FullUserDto dto = new FullUserDto(makeUser("email@email.co.nz","password123",false));
        String string = "1";
        String badField = string.repeat(51);
        dto.setFirstName(badField);
        dto.setLastName(badField);
        dto.setMiddleName(badField);
        dto.setNickname(badField);
        dto.setBio(string.repeat(251));
        dto.setEmail(badField + "@email.com");
        dto.setPhoneNumber(string.repeat(26));
        Assertions.assertEquals(7, validate(dto).size());
    }

    @Test
    public void invalidFullUserDtoWithBlankFields() {
        FullUserDto dto = new FullUserDto(makeUser("email@email.co.nz","password123",false));
        dto.setFirstName("");
        dto.setLastName("");
        Assertions.assertEquals(2, validate(dto).size());
    }

    @Test
    public void invalidFullUserDtoWithNullFields() {
        FullUserDto dto = new FullUserDto(makeUser("email@email.co.nz","password123",false));
        dto.setDateOfBirth(null);
        Assertions.assertEquals(1, validate(dto).size());
    }


}
