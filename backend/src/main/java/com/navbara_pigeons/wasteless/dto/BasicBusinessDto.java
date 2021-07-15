package com.navbara_pigeons.wasteless.dto;

import com.navbara_pigeons.wasteless.entity.Business;
import java.time.ZonedDateTime;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Business DTO which returns all user details except catalog, administrators list and image list
 */
@Data
public class BasicBusinessDto {

  private long id;
  @NotNull(message = "Primary Administrator Id cannot be Null")
  private long primaryAdministratorId;

  @Max(message = "Name has to be less than or equal to 50 Characters", value = 50)
  @NotBlank(message = "Name is Required")
  private String name;

  @Max(message = "Description has to be less than or equal to 250 Characters", value = 250)
  private String description;
  private FullAddressDto address;

  @NotNull(message = "Business Type is Required")
  @Max(message = "Business Type has to be less than or equal to 50 Characters", value = 50)
  private String businessType;
  private ZonedDateTime created;

  public BasicBusinessDto(Business business) {
    this.id = business.getId();
    this.primaryAdministratorId = business.getPrimaryAdministratorId();
    this.name = business.getName();
    this.description = business.getDescription();
    this.address = new FullAddressDto(business.getAddress());
    this.businessType = business.getBusinessType();
    this.created = business.getCreated();
  }

  public BasicBusinessDto() {

  }

}
