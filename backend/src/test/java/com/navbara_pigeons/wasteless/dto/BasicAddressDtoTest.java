package com.navbara_pigeons.wasteless.dto;

import com.navbara_pigeons.wasteless.testprovider.MainTestProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

public class BasicAddressDtoTest extends MainTestProvider {
    private Set<ConstraintViolation<BasicAddressDto>> validate(BasicAddressDto dto) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<BasicAddressDto>> violations = validator.validate(dto);
        return violations;
    }

    @Test
    public void validBusinessType() {
        Assertions.assertEquals(0, validate(new BasicAddressDto(makeAddress())).size());
    }

    @Test
    public void invalidBasicAddressDtoWithLargeLengths() {
        BasicAddressDto dto = new BasicAddressDto(makeAddress());
        String string = "1";
        String badField = string.repeat(201);
        dto.setCity(badField);
        dto.setRegion(badField);
        dto.setCountry(badField);

        Assertions.assertEquals(3, validate(dto).size());
    }

    @Test
    public void invalidBasicAddressDtoWithNullFields() {
        BasicAddressDto dto = new BasicAddressDto(makeAddress());
        dto.setCountry(null);
        Assertions.assertEquals(1, validate(dto).size());
    }

    @Test
    public void invalidBasicAddressDtoWithBlankFields() {
        BasicAddressDto dto = new BasicAddressDto(makeAddress());
        dto.setCountry("");
        Assertions.assertEquals(1, validate(dto).size());
    }
}
