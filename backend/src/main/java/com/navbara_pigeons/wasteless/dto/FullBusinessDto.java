package com.navbara_pigeons.wasteless.dto;

import com.navbara_pigeons.wasteless.entity.Business;
import com.navbara_pigeons.wasteless.entity.Product;
import com.navbara_pigeons.wasteless.entity.User;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Business DTO which returns all user details except catalog and administrators list
 */
@Data
public class FullBusinessDto {

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
  private List<BasicUserDto> administrators;
  private List<BasicProductDto> productsCatalogue;

  public FullBusinessDto(Business business, String publicPathPrefix) {
    this.id = business.getId();
    this.primaryAdministratorId = business.getPrimaryAdministratorId();
    this.name = business.getName();
    this.description = business.getDescription();
    this.address = new FullAddressDto(business.getAddress());
    this.businessType = business.getBusinessType();
    this.created = business.getCreated();
    if (business.getAdministrators() != null) {
      this.administrators = makeUserDto(business.getAdministrators());
    }
    if (business.getProductsCatalogue() != null) {
      this.productsCatalogue = makeProductDto(business.getProductsCatalogue(), publicPathPrefix);
    }
  }

  public FullBusinessDto() {

  }

  private List<BasicUserDto> makeUserDto(List<User> users) {
    ArrayList<BasicUserDto> userlistDto = new ArrayList<>();
    for (User user : users) {
      BasicUserDto userDto = new BasicUserDto(user);
      userDto.setBusinesses(new ArrayList<>());
      userlistDto.add(userDto);
    }
    return userlistDto;
  }

  private List<BasicProductDto> makeProductDto(List<Product> products, String publicPathPrefix) {
    ArrayList<BasicProductDto> productsDto = new ArrayList<BasicProductDto>();
    for (Product product : products) {
      productsDto.add(new BasicProductDto(product, publicPathPrefix));
    }
    return productsDto;
  }

}
