package com.navbara_pigeons.wasteless.dto;

import com.navbara_pigeons.wasteless.testprovider.MainTestProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

public class FullBusinessDtoTest extends MainTestProvider {
    private Set<ConstraintViolation<FullBusinessDto>> validate(FullBusinessDto dto) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<FullBusinessDto>> violations = validator.validate(dto);
        return violations;
    }

    @Test
    public void validBusinessType() {
        Assertions.assertEquals(0, validate(new FullBusinessDto(makeBusiness(), "/test")).size());
    }

    @Test
    public void invalidFullBusinessDtoWithLargeLengths() {
        FullBusinessDto dto = new FullBusinessDto(makeBusiness(),"/test");
        String string = "1";
        String badDescription = string.repeat(251);
        String badName = string.repeat(51);
        dto.setDescription(badDescription);
        dto.setName(badName);

        Assertions.assertEquals(2, validate(dto).size());
    }

    @Test
    public void invalidFullBusinessDtoWithNullFields() {
        FullBusinessDto dto = new FullBusinessDto(makeBusiness(),"/test");
        dto.setName(null);
        dto.setBusinessType(null);
        Assertions.assertEquals(2, validate(dto).size());
    }
}
