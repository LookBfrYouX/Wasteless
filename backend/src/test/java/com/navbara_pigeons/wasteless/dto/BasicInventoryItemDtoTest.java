package com.navbara_pigeons.wasteless.dto;

import com.navbara_pigeons.wasteless.testprovider.MainTestProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

public class BasicInventoryItemDtoTest extends MainTestProvider {
    private Set<ConstraintViolation<BasicInventoryItemDto>> validate(BasicInventoryItemDto dto) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<BasicInventoryItemDto>> violations = validator.validate(dto);
        return violations;
    }

    @Test
    public void validInventoryItemType() {
        Assertions.assertEquals(0, validate(new BasicInventoryItemDto(makeInventoryItem(makeProduct("new"),makeBusiness()), "/test")).size());
    }

    @Test
    public void invalidBasicInventoryItemDtoWithNullFields() {
        BasicInventoryItemDto dto = new BasicInventoryItemDto(makeInventoryItem(makeProduct("new"),makeBusiness()), "/test");
        dto.setExpires(null);
        Assertions.assertEquals(1, validate(dto).size());
    }

    @Test
    public void invalidBasicInventoryItemDtoWithNegativeFields() {
        BasicInventoryItemDto dto = new BasicInventoryItemDto(makeInventoryItem(makeProduct("new"),makeBusiness()), "/test");
        dto.setQuantity(-1);
        Assertions.assertEquals(1, validate(dto).size());
    }
}
