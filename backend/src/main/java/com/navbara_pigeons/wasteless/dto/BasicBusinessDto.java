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

/**
 * Business DTO which returns all user details except catalog, administrators list and image list
 */
@Data
public class BasicBusinessDto {

    private long id;
    private long primaryAdministratorId;
    private String name;
    private String description;
    private FullAddressDto address;
    private String businessType;
    private ZonedDateTime created;

    public BasicBusinessDto(Business business) {
        this.id = business.getId();
        this.primaryAdministratorId = business.getPrimaryAdministratorId();
        this.name = business.getName();
        this.description = business.getDescription();
        this.address = new FullAddressDto(business.getAddress());
        this.businessType = business.getBusinessType();
        this.created = business.getCreated();
    }

    public BasicBusinessDto() {

    }

}
