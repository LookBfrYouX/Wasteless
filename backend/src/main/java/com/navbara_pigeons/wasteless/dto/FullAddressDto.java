package com.navbara_pigeons.wasteless.dto;

import com.navbara_pigeons.wasteless.entity.Address;
import lombok.Data;

/**
 * Address DTO which all properties except ID
 */
@Data
public class FullAddressDto {

//    private long id;
    private String streetNumber;
    private String streetName;
    private String postcode;
    private String city;
    private String region;
    private String country;

    public FullAddressDto(Address address) {
        this.streetNumber = address.getStreetNumber();
        this.streetName = address.getStreetName();
        this.postcode = address.getPostcode();
        this.city = address.getCity();
        this.region = address.getRegion();
        this.country = address.getCountry();
    }

    public FullAddressDto() {

    }

}
