package com.navbara_pigeons.wasteless.dto;

import com.navbara_pigeons.wasteless.entity.Address;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Max;
/**
 * Address DTO which only returns some coarse address properties like city, region and country
 */
@Data
public class BasicAddressDto {

  @Length(max=200, message = "City has to be less than or equal to 100 Characters")
  private String city;

  @Length(max=200, message = "Region has to be less than or equal to 100 Characters")
  private String region;

  @NotBlank(message = "Country is Required")
  @Length(max=100, message = "Country has to be less than or equal to 100 Characters")
  private String country;

  public BasicAddressDto(Address address) {
    this.city = address.getCity();
    this.region = address.getRegion();
    this.country = address.getCountry();
  }

  public BasicAddressDto() {

  }

}
