package com.navbara_pigeons.wasteless.dto;

import com.navbara_pigeons.wasteless.entity.Business;
import com.navbara_pigeons.wasteless.entity.BusinessType;
import com.navbara_pigeons.wasteless.validation.constraints.StringEnumeration;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;


/**
 * Business DTO which returns all user details except catalog and administrators list
 */
@Data
public class CreateBusinessDto {

  @NotNull(message = "Primary administrator ID is required")
  @Positive
  private Long primaryAdministratorId;

  @NotBlank
  @Size(max = 50, message = "Name too long; max 50 characters")
  private String name;

  @Size(max = 250, message = "Description too long; max 250 characters")
  private String description;

  @NotNull(message = "Address is required")
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
