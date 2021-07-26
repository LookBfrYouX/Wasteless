package com.navbara_pigeons.wasteless.dto;

import com.navbara_pigeons.wasteless.testprovider.MainTestProvider;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

public class CreateInventoryItemDtoTest extends MainTestProvider {
    private Set<ConstraintViolation<CreateInventoryItemDto>> validate(CreateInventoryItemDto dto) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<CreateInventoryItemDto>> violations = validator.validate(dto);
        return violations;
    }

    @Test
    public void validInventoryItemType() {
        Assertions.assertEquals(0, validate(new CreateInventoryItemDto(makeInventoryItem(makeProduct("new"),makeBusiness()))).size());
    }

    @Test
    public void invalidCreateInventoryItemDtoWithNullFields() {
        CreateInventoryItemDto dto = new CreateInventoryItemDto(makeInventoryItem(makeProduct("new"),makeBusiness()));
        dto.setExpires(null);
        Assertions.assertEquals(1, validate(dto).size());
    }

    @Test
    public void invalidCreateInventoryItemDtoWithNegativeFields() {
        CreateInventoryItemDto dto = new CreateInventoryItemDto(makeInventoryItem(makeProduct("new"),makeBusiness()));
        dto.setQuantity(-1);
        Assertions.assertEquals(1, validate(dto).size());
    }
    
}
