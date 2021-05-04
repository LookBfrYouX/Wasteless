package com.navbara_pigeons.wasteless.dto;

import com.navbara_pigeons.wasteless.entity.Image;
import com.navbara_pigeons.wasteless.entity.Product;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Product DTO which contains only information required when creating a product
 */
@Data
public class BasicProductCreationDto {

    private String name;
    private String description;
    private String manufacturer;
    private Double recommendedRetailPrice;


    public BasicProductCreationDto() {

    }

    public BasicProductCreationDto(Product product) {
        this.name = product.getName();
        this.description = product.getDescription();
        this.manufacturer = product.getManufacturer();
        this.recommendedRetailPrice = product.getRecommendedRetailPrice();
    }

}
