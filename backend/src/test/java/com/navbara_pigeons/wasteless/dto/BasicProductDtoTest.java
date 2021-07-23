package com.navbara_pigeons.wasteless.dto;

import com.navbara_pigeons.wasteless.testprovider.MainTestProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

public class BasicProductDtoTest extends MainTestProvider {
    private Set<ConstraintViolation<BasicProductDto>> validate(BasicProductDto dto) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<BasicProductDto>> violations = validator.validate(dto);
        return violations;
    }

    @Test
    public void validBusinessType() {
        Assertions.assertEquals(0, validate(new BasicProductDto(makeProduct("product"),"/test")).size());
    }

    @Test
    public void invalidBasicProductDtoWithLargeLengths() {
        BasicProductDto dto = new BasicProductDto(makeProduct("product"),"/test");
        String string = "1";
        dto.setName(string.repeat(101));
        dto.setDescription(string.repeat(501));
        dto.setManufacturer(string.repeat(101));
        Assertions.assertEquals(3, validate(dto).size());
    }

    @Test
    public void invalidBasicProductDtoWithNegativeFields() {
        BasicProductDto dto = new BasicProductDto(makeProduct("product"),"/test");
        dto.setRecommendedRetailPrice(-1.0);
        Assertions.assertEquals(1, validate(dto).size());
    }

    @Test
    public void invalidBasicProductDtoWithBlankFields() {
        BasicProductDto dto = new BasicProductDto(makeProduct(""),"/test");
        Assertions.assertEquals(1, validate(dto).size());
    }
}
