package com.navbara_pigeons.wasteless.dto;

import com.navbara_pigeons.wasteless.testprovider.MainTestProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

public class BasicBusinessDtoTest extends MainTestProvider {
    private Set<ConstraintViolation<BasicBusinessDto>> validate(BasicBusinessDto dto) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<BasicBusinessDto>> violations = validator.validate(dto);
        return violations;
    }

    @Test
    public void validBusinessType() {
        Assertions.assertEquals(0, validate(new BasicBusinessDto(makeBusiness())).size());
    }

    @Test
    public void invalidBasicBusinessDtoWithLargeLengths() {
        BasicBusinessDto dto = new BasicBusinessDto(makeBusiness());
        String string = "1";
        String badDescription = string.repeat(251);
        String badName = string.repeat(51);
        dto.setDescription(badDescription);
        dto.setName(badName);

        Assertions.assertEquals(2, validate(dto).size());
    }

    @Test
    public void invalidBasicBusinessDtoWithNullFields() {
        BasicBusinessDto dto = new BasicBusinessDto(makeBusiness());
        dto.setName(null);
        dto.setBusinessType(null);
        Assertions.assertEquals(2, validate(dto).size());
    }
}
