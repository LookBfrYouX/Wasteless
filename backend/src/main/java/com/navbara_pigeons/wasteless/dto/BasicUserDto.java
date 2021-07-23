package com.navbara_pigeons.wasteless.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.navbara_pigeons.wasteless.entity.Business;
import com.navbara_pigeons.wasteless.entity.User;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;

/**
 * User DTO which does not return coarse address, email, date of birth and phone number
 */
@Data
public class BasicUserDto {

  private long id;

  @NotBlank(message = "First Name is Required")
  @Length(max=50, message = "First Name has to be less than or equal to 50 Characters")
  private String firstName;

  @NotBlank(message = "Last Name is Required")
  @Length(max=50, message = "Last Name has to be less than or equal to 50 Characters")
  private String lastName;

  @Length(max=50, message = "Middle Name has to be less than or equal to 50 Characters")
  private String middleName;

  @Length(max=50, message = "Nick Name has to be less than or equal to 50 Characters")
  private String nickname;

  @Length(max=250, message = "Bio has to be less than or equal to 250 Characters")
  private String bio;
  private BasicAddressDto homeAddress;
  private ZonedDateTime created;
  private String role;
  @JsonProperty("businessesAdministered")
  private List<BasicBusinessDto> businesses;

  public BasicUserDto(User user) {
    this.id = user.getId();
    this.firstName = user.getFirstName();
    this.lastName = user.getLastName();
    this.middleName = user.getMiddleName();
    this.nickname = user.getNickname();
    this.bio = user.getBio();
    this.created = user.getCreated();
    this.role = user.getRole();
    this.homeAddress = new BasicAddressDto(user.getHomeAddress());
    if (user.getBusinesses() != null) {
      this.businesses = makeBusinessDto(user.getBusinesses());
    }
  }

  public BasicUserDto() {

  }

  private List<BasicBusinessDto> makeBusinessDto(List<Business> businesses) {
    ArrayList<BasicBusinessDto> businessDtos = new ArrayList<BasicBusinessDto>();
    for (Business business : businesses) {
      businessDtos.add(new BasicBusinessDto(business));
    }
    return businessDtos;
  }

}
