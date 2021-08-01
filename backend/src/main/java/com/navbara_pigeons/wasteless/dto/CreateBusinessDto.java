package com.navbara_pigeons.wasteless.dto;

import com.navbara_pigeons.wasteless.entity.Business;
import com.navbara_pigeons.wasteless.entity.BusinessType;
import com.navbara_pigeons.wasteless.validation.constraints.StringEnumeration;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;


/**
 * Business DTO which returns all user details except catalog and administrators list
 */
@Data
public class CreateBusinessDto {

  private Long primaryAdministratorId;

  @NotBlank(message = "Name is Required")
  @Length(max = 50, message = "Name has to be less than or equal to 50 Characters")
  private String name;

  @Length(max = 250, message = "Description has to be less than or equal to 250 Characters")
  private String description;

  @Valid
  private FullAddressDto address;

  @NotNull(message = "Business Type is Required")
  @Length(max = 50, message = "Business Type has to be less than or equal to 50 Characters")
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
