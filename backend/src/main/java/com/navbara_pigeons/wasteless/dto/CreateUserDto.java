package com.navbara_pigeons.wasteless.dto;

import com.navbara_pigeons.wasteless.entity.User;
import java.time.LocalDate;
import lombok.Data;
import org.hibernate.validator.constraints.UniqueElements;

import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


/**
 * User DTO which returns all user information except password
 */
@Data
public class CreateUserDto {

  @NotBlank(message = "First Name is Required")
  @Max(message = "First Name has to be less than or equal to 50 Characters", value = 50)
  private String firstName;

  @NotBlank(message = "Last Name is Required")
  @Max(message = "Last Name has to be less than or equal to 50 Characters", value = 50)
  private String lastName;

  @Max(message = "Middle Name has to be less than or equal to 50 Characters", value = 50)
  private String middleName;

  @Max(message = "Nickname has to be less than or equal to 50 Characters", value = 50)
  private String nickname;

  @Max(message = "Bio has to be less than or equal to 250 Characters", value = 250)
  private String bio;

  @Email(message = "Email has to be Valid")
  @UniqueElements(message = "Email has to be Unique")
  @Max(message = "Email has to be less than or equal to 50 Characters", value = 50)
  private String email;

  @NotNull(message = "Date Of Birth cannot be Null")
  private LocalDate dateOfBirth;

  @Max(message = "Phone Number has to be less than or equal to 25 Characters", value = 25)
  private String phoneNumber;

  @NotBlank(message = "Password is Required")
  @Max(message = "Password has to be less than or equal to 60 Characters", value = 60)
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
