package com.navbara_pigeons.wasteless.dto;

import com.navbara_pigeons.wasteless.entity.Business;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


/**
 * Business DTO which returns all user details except catalog and administrators list
 */
@Data
public class CreateBusinessDto {

  private long primaryAdministratorId;

  @NotBlank(message = "Name is Required")
  @Length(max=50, message = "Name has to be less than or equal to 50 Characters")
  private String name;

  @Length(max=250, message = "Description has to be less than or equal to 250 Characters")
  private String description;

  @Valid
  private FullAddressDto address;

  @NotNull(message = "Business Type is Required")
  @Length(max=50, message = "Business Type has to be less than or equal to 50 Characters")
  private String businessType;

  public CreateBusinessDto(Business business) {
    this.primaryAdministratorId = business.getPrimaryAdministratorId();
    this.name = business.getName();
    this.description = business.getDescription();
    this.address = new FullAddressDto(business.getAddress());
    this.businessType = business.getBusinessType();
  }

  public CreateBusinessDto() {

  }

}
