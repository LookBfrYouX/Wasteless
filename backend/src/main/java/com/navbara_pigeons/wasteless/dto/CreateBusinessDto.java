package com.navbara_pigeons.wasteless.dto;

import com.navbara_pigeons.wasteless.entity.Business;
import com.navbara_pigeons.wasteless.entity.BusinessType;
import com.navbara_pigeons.wasteless.validation.constraints.StringEnumeration;
import lombok.Data;


/**
 * Business DTO which returns all user details except catalog and administrators list
 */
@Data
public class CreateBusinessDto {

  private long primaryAdministratorId;
  private String name;
  private String description;
  private FullAddressDto address;
  
  @StringEnumeration(enumClass = BusinessType.class, message = "Invalid business type given")
  private String businessType;

  public CreateBusinessDto(Business business) {
    this.primaryAdministratorId = business.getPrimaryAdministratorId();
    this.name = business.getName();
    this.description = business.getDescription();
    this.address = new FullAddressDto(business.getAddress());
    this.businessType = business.getBusinessType().toString();
  }

  public CreateBusinessDto() {

  }

}
