package com.navbara_pigeons.wasteless.dto;

import com.navbara_pigeons.wasteless.entity.Address;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Max;
/**
 * Address DTO which only returns some coarse address properties like city, region and country
 */
@Data
public class BasicAddressDto {

  @Max(message = "City has to be less than or equal to 200 Characters", value = 200)
  private String city;

  @Max(message = "Region has to be less than or equal to 200 Characters", value = 200)
  private String region;

  @Max(message = "Country has to be less than or equal to 100 Characters", value = 100)
  @NotBlank(message = "Country is Required")
  private String country;

  public BasicAddressDto(Address address) {
    this.city = address.getCity();
    this.region = address.getRegion();
    this.country = address.getCountry();
  }

  public BasicAddressDto() {

  }

}
