package com.navbara_pigeons.wasteless.dto;

import com.navbara_pigeons.wasteless.entity.Address;
import com.navbara_pigeons.wasteless.entity.Business;
import com.navbara_pigeons.wasteless.entity.Product;
import com.navbara_pigeons.wasteless.entity.User;
import lombok.Data;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class BasicBusinessDto {

    private long id;
    private long primaryAdministratorId;
    private String name;
    private String description;
    private FullAddressDto address;
    private String businessType;
    private ZonedDateTime created;
//    private List<BasicUserDto> administrators;
//    private List<BasicProductDto> productsCatalogue;

    public BasicBusinessDto(Business business) {
        this.id = business.getId();
        this.primaryAdministratorId = business.getPrimaryAdministratorId();
        this.name = business.getName();
        this.description = business.getDescription();
        this.address = new FullAddressDto(business.getAddress());
        this.businessType = business.getBusinessType();
        this.created = business.getCreated();
//        if (business.getAdministrators() != null) {
//            this.administrators = makeUserDto(business.getAdministrators());
//        }
//        if (business.getProductsCatalogue() != null) {
//            this.productsCatalogue = makeProductDto(business.getProductsCatalogue());
//        }
    }

    private List<BasicUserDto> makeUserDto(List<User> users) {
        ArrayList<BasicUserDto> userlistDto = new ArrayList<>();
        for (User user : users) {
            user.setBusinesses(null);
            userlistDto.add(new BasicUserDto(user));
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
