package com.navbara_pigeons.wasteless.dto;

import com.navbara_pigeons.wasteless.entity.Address;
import lombok.Data;

/**
 * Address DTO which only returns some coarse address properties like city, region and country
 */
@Data
public class BasicAddressDto {

  private String city;
  private String region;
  private String country;

  /**
   * Constructor to create Address DTO from Address Entity
   *
   * @param address
   */
  public BasicAddressDto(Address address) {
    this.city = address.getCity();
    this.region = address.getRegion();
    this.country = address.getCountry();
  }

  public BasicAddressDto() {

  }

}
