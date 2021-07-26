package com.navbara_pigeons.wasteless.dto;

import com.navbara_pigeons.wasteless.testprovider.MainTestProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

public class FullAddressDtoTest extends MainTestProvider {
    private Set<ConstraintViolation<FullAddressDto>> validate(FullAddressDto dto) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<FullAddressDto>> violations = validator.validate(dto);
        return violations;
    }

    @Test
    public void validBusinessType() {
        Assertions.assertEquals(0, validate(new FullAddressDto(makeAddress())).size());
    }

    @Test
    public void invalidFullAddressDtoWithLargeLengths() {
        FullAddressDto dto = new FullAddressDto(makeAddress());
        String string = "1";
        String badField = string.repeat(201);
        dto.setCity(badField);
        dto.setRegion(badField);
        dto.setCountry(badField);

        Assertions.assertEquals(3, validate(dto).size());
    }

    @Test
    public void invalidFullAddressDtoWithNullFields() {
        FullAddressDto dto = new FullAddressDto(makeAddress());
        dto.setCountry(null);
        Assertions.assertEquals(1, validate(dto).size());
    }

    @Test
    public void invalidFullAddressDtoWithBlankFields() {
        FullAddressDto dto = new FullAddressDto(makeAddress());
        dto.setCountry("");
        Assertions.assertEquals(1, validate(dto).size());
    }
}
