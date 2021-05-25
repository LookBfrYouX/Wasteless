package com.navbara_pigeons.wasteless.dto;

import com.navbara_pigeons.wasteless.entity.User;
import lombok.Data;

import java.time.LocalDate;


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
