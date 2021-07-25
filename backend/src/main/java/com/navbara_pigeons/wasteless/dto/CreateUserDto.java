package com.navbara_pigeons.wasteless.dto;

import com.navbara_pigeons.wasteless.entity.User;
import java.time.LocalDate;

import com.navbara_pigeons.wasteless.validation.constraints.NotTooDistantPast;
import lombok.Data;

import javax.validation.constraints.NotNull;


/**
 * User DTO which returns all user information except password
 */
@Data
public class CreateUserDto {

  private String firstName;
  private String lastName;
  private String middleName;
  private String nickname;
  private String bio;
  private String email;

  @NotNull(message = "Date of birth is required")
  @NotTooDistantPast(message = "Date of birth too far into the past")
  private LocalDate dateOfBirth;
  private String phoneNumber;
  private String password;
  private FullAddressDto homeAddress;

  public CreateUserDto(User user) {
    this.firstName = user.getFirstName();
    this.lastName = user.getLastName();
    this.middleName = user.getMiddleName();
    this.nickname = user.getNickname();
    this.bio = user.getBio();
    this.email = user.getEmail();
    this.dateOfBirth = user.getDateOfBirth();
    this.phoneNumber = user.getPhoneNumber();
    this.password = user.getPassword();
    this.homeAddress = new FullAddressDto(user.getHomeAddress());
  }

  public CreateUserDto() {

  }

}
