package com.navbara_pigeons.wasteless.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.navbara_pigeons.wasteless.entity.Image;
import com.navbara_pigeons.wasteless.entity.Product;
import lombok.Data;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Products DTO which returns all product information except `currency` and `primaryProductImage` (returned as the first element in the image list)
 */
@Data
public class BasicProductDto {

    private long id;
    private String name;
    private String description;
//    private String currency;
    private String manufacturer;
    private Double recommendedRetailPrice;
    private ZonedDateTime created;
    private BasicImageDto primaryProductImage;
    private List<BasicImageDto> productImages;

    public BasicProductDto(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.description = product.getDescription();
        this.currency = product.getCurrency();
        this.manufacturer = product.getManufacturer();
        this.recommendedRetailPrice = product.getRecommendedRetailPrice();
        this.created = product.getCreated();
//        if (product.getPrimaryProductImage() != null) {
//            this.primaryProductImage = new BasicImageDto(product.getPrimaryProductImage());
//        }
        if (product.getProductImages() != null) {
            this.productImages = makeImageDtos(product.getImages()); // First image is primary image
        }
    }

    private List<BasicImageDto> makeImageDtos(List<Image> images) {
        ArrayList<BasicImageDto> imageDtos = new ArrayList<BasicImageDto>();
        for (Image image : images) {
            imageDtos.add(new BasicImageDto(image));
        }
        return imageDtos;
    }

}