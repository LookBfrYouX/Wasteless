package com.navbara_pigeons.wasteless.dto;

import com.navbara_pigeons.wasteless.testprovider.MainTestProvider;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

public class CreateListingDtoTest extends MainTestProvider {
    private Set<ConstraintViolation<CreateListingDto>> validate(CreateListingDto dto) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<CreateListingDto>> violations = validator.validate(dto);
        return violations;
    }

    private CreateListingDto makeCreateListingDto() {
        CreateListingDto createListingDto = new CreateListingDto();
        createListingDto.setQuantity(1);
        createListingDto.setInventoryItemId(5001);
        return createListingDto;
    }

    @Test
    public void invalidCreateListingClosesDateInPast() {
        CreateListingDto dto = makeCreateListingDto();
        dto.setCloses(ZonedDateTime.now(ZoneId.systemDefault()).minusDays(1));
        Assertions.assertEquals(1, validate(dto).size());
    }

    @Test
    public void invalidCreateInventoryItemExpiryDateTooFarInFuture() {
        CreateListingDto dto = makeCreateListingDto();
        dto.setCloses(ZonedDateTime.now(ZoneId.systemDefault()).plusYears(15));
        Assertions.assertEquals(1, validate(dto).size());
    }

    @Test
    public void validCreateInventoryItemExpiryDateValid() {
        CreateListingDto dto = makeCreateListingDto();
        dto.setCloses(ZonedDateTime.now(ZoneId.systemDefault()).plusDays(1));
        Assertions.assertEquals(0, validate(dto).size());
    }
}
