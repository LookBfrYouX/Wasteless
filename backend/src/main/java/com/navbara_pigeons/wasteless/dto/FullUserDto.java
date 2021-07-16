package com.navbara_pigeons.wasteless.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.navbara_pigeons.wasteless.entity.Business;
import com.navbara_pigeons.wasteless.entity.User;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.UniqueElements;

import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * User DTO which returns all user information except password
 */
@Data
public class FullUserDto {

  private long id;

  @NotBlank(message = "First Name is Required")
  @Length(max=50, message = "First Name has to be less than or equal to 50 Characters")
  private String firstName;

  @NotBlank(message = "Last Name is Required")
  @Length(max=50, message = "Last Name has to be less than or equal to 50 Characters")
  private String lastName;

  @Length(max=50, message = "Middle Name has to be less than or equal to 50 Characters")
  private String middleName;

  @Length(max=50, message = "Nickname has to be less than or equal to 50 Characters")
  private String nickname;

  @Length(max=250, message = "Bio has to be less than or equal to 250 Characters")
  private String bio;

  @Email(message = "Email has to be Valid")
  @UniqueElements(message = "Email has to be Unique")
  @Length(max=50, message = "Email has to be less than or equal to 50 Characters")
  private String email;

  @NotNull(message = "Date Of Birth cannot be Null")
  private LocalDate dateOfBirth;

  @Length(max=25, message = "Phone Number has to be less than or equal to 25 Characters")
  private String phoneNumber;
  private FullAddressDto homeAddress;

  //Calculated Value
  private ZonedDateTime created;

  //Calculated Value
  private String role;
  @JsonProperty("businessesAdministered")
  private List<BasicBusinessDto> businesses;

  public FullUserDto(User user) {
    this.id = user.getId();
    this.firstName = user.getFirstName();
    this.lastName = user.getLastName();
    this.middleName = user.getMiddleName();
    this.nickname = user.getNickname();
    this.bio = user.getBio();
    this.email = user.getEmail();
    this.dateOfBirth = user.getDateOfBirth();
    this.phoneNumber = user.getPhoneNumber();
    this.created = user.getCreated();
    this.role = user.getRole();
    this.homeAddress = new FullAddressDto(user.getHomeAddress());
    if (user.getBusinesses() != null) {
      this.businesses = makeBusinessDto(user.getBusinesses());
    }
  }

  public FullUserDto() {

  }

  private List<BasicBusinessDto> makeBusinessDto(List<Business> businesses) {
    ArrayList<BasicBusinessDto> businessDtos = new ArrayList<BasicBusinessDto>();
    for (Business business : businesses) {
      businessDtos.add(new BasicBusinessDto(business));
    }
    return businessDtos;
  }

}
