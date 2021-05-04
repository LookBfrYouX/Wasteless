package com.navbara_pigeons.wasteless.dto;

import com.navbara_pigeons.wasteless.entity.Address;
import lombok.Data;

/**
 * Address DTO which only returns some coarse address properties like city, region and country
 */
@Data
public class BasicAddressDto {

//    private long id;
//    private String streetNumber;
//    private String streetName;
//    private String postcode;
    private String city;
    private String region;
    private String country;
    public BasicAddressDto(Address address) {
//        this.streetNumber = address.getStreetNumber();
//        this.streetName = address.getStreetName();
//        this.postcode = address.getPostcode();
        this.city = address.getCity();
        this.region = address.getRegion();
        this.country = address.getCountry();
    }

}
