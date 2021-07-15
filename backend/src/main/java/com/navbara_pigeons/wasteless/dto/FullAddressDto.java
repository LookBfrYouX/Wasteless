package com.navbara_pigeons.wasteless.dto;

import com.navbara_pigeons.wasteless.entity.Address;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;

/**
 * Address DTO which all properties except ID
 */
@Data
public class FullAddressDto {

  @Max(message = "Street Number has to be less than or equal to 100 Characters", value = 100)
  private String streetNumber;

  @Max(message = "Street Name has to be less than or equal to 200 Characters", value = 200)
  private String streetName;

  @Max(message = "Suburb has to be less than or equal to 200 Characters", value = 200)
  private String suburb;

  @Max(message = "Postcode has to be less than or equal to 30 Characters", value = 30)
  private String postcode;

  @Max(message = "City has to be less than or equal to 200 Characters", value = 200)
  private String city;

  @Max(message = "Region has to be less than or equal to 200 Characters", value = 200)
  private String region;

  @Max(message = "Country has to be less than or equal to 100 Characters", value = 100)
  @NotBlank(message = "Country is Required")
  private String country;

  public FullAddressDto(Address address) {
    this.streetNumber = address.getStreetNumber();
    this.streetName = address.getStreetName();
    this.suburb = address.getSuburb();
    this.postcode = address.getPostcode();
    this.city = address.getCity();
    this.region = address.getRegion();
    this.country = address.getCountry();
  }

  public FullAddressDto() {

  }

}
