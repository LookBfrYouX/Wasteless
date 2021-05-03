package com.navbara_pigeons.wasteless.dto;

import com.navbara_pigeons.wasteless.entity.Business;
import com.navbara_pigeons.wasteless.entity.Product;
import com.navbara_pigeons.wasteless.entity.User;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Business DTO which returns all user details except catalog and administrators list
 */
@Data
public class FullBusinessDto {

    private long id;
    private long primaryAdministratorId;
    private String name;
    private String description;
    private FullAddressDto address;
    private String businessType;
    private ZonedDateTime created;
    private List<FullUserDto> administrators;
    private List<BasicProductDto> productsCatalogue;

    public FullBusinessDto(Business business) {
        this.id = business.getId();
        this.primaryAdministratorId = business.getPrimaryAdministratorId();
        this.name = business.getName();
        this.description = business.getDescription();
        this.address = new FullAddressDto(business.getAddress());
        this.businessType = business.getBusinessType();
        this.created = business.getCreated();
        if (business.getAdministrators() != null) {
            this.administrators = makeUserDto(business.getAdministrators());
        }
        if (business.getProductsCatalogue() != null) {
            this.productsCatalogue = makeProductDto(business.getProductsCatalogue());
        }
    }

    private List<FullUserDto> makeUserDto(List<User> users) {
        ArrayList<FullUserDto> userlistDto = new ArrayList<>();
        for (User user : users) {
            user.setBusinesses(null);
            userlistDto.add(new FullUserDto(user));
        }
        return userlistDto;
    }

    private List<BasicProductDto> makeProductDto(List<Product> products) {
        ArrayList<BasicProductDto> productsDto = new ArrayList<BasicProductDto>();
        for (Product product : products) {
            productsDto.add(new BasicProductDto(product));
        }
        return productsDto;
    }

}
