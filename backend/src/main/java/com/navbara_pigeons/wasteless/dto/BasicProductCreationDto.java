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

//    private long id;
    private String name;
    private String description;
//    private String currency;
    private String manufacturer;
    private Double recommendedRetailPrice;
//    private ZonedDateTime created;
//    private BasicImageDto primaryProductImage;
//    private List<BasicImageDto> productImages;

    public BasicProductCreationDto() {}

    public BasicProductCreationDto(Product product) {
//        this.id = product.getId();
        this.name = product.getName();
        this.description = product.getDescription();
        //this.currency = product.getCurrency();
        this.manufacturer = product.getManufacturer();
        this.recommendedRetailPrice = product.getRecommendedRetailPrice();
//        this.created = product.getCreated();
//        if (product.getPrimaryProductImage() != null) {
//            this.primaryProductImage = new BasicImageDto(product.getPrimaryProductImage());
//        }
//        if (product.getProductImages() != null) {
//            this.productImages = makeImageDtos(product.getImages()); // First image is primary image
//        }
    }

//    private List<BasicImageDto> makeImageDtos(List<Image> images) {
//        ArrayList<BasicImageDto> imageDtos = new ArrayList<BasicImageDto>();
//        for (Image image : images) {
//            imageDtos.add(new BasicImageDto(image));
//        }
//        return imageDtos;
//    }
}
