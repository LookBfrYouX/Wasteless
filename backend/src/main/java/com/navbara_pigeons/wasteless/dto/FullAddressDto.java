package com.navbara_pigeons.wasteless.dto;

import com.navbara_pigeons.wasteless.entity.Address;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;

/**
 * Address DTO which all properties except ID
 */
@Data
public class FullAddressDto {

  @Length(max=100, message = "Street Number has to be less than or equal to 100 Characters")
  private String streetNumber;

  @Length(max=200, message = "Street Name has to be less than or equal to 200 Characters")
  private String streetName;

  @Length(max=200, message = "Suburb has to be less than or equal to 200 Characters")
  private String suburb;

  @Length(max=30, message = "Postcode has to be less than or equal to 30 Characters")
  private String postcode;

  @Length(max=200, message = "City has to be less than or equal to 200 Characters")
  private String city;

  @Length(max=200, message = "Region has to be less than or equal to 200 Characters")
  private String region;

  @Length(max=100, message = "Country has to be less than or equal to 100 Characters")
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
