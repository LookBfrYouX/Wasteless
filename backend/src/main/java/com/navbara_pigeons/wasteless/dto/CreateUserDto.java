package com.navbara_pigeons.wasteless.dto;

import com.navbara_pigeons.wasteless.entity.User;
import com.navbara_pigeons.wasteless.validation.constraints.AfterNowPlusXYears;
import com.navbara_pigeons.wasteless.validation.constraints.BeforeNowPlusXYears;
import java.time.LocalDate;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * User DTO which returns all user information except password
 */
@Data
public class CreateUserDto {

  @NotBlank(message = "First Name is Required")
  @Length(max = 50, message = "First Name has to be less than or equal to 50 Characters")
  private String firstName;

  @NotBlank(message = "Last Name is Required")
  @Length(max = 50, message = "Last Name has to be less than or equal to 50 Characters")
  private String lastName;

  @Length(max = 50, message = "Middle Name has to be less than or equal to 50 Characters")
  private String middleName;

  @Length(max = 50, message = "Nickname has to be less than or equal to 50 Characters")
  private String nickname;

  @Length(max = 250, message = "Bio has to be less than or equal to 250 Characters")
  private String bio;

  @Pattern(regexp = "^[a-zA-Z0-9.!#$%&'*+\\/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)+$", message = "Email has to be Valid")
  @Length(max = 50, message = "Email has to be less than or equal to 50 Characters")
  private String email;

  @NotNull(message = "Date Of Birth cannot be Null")
  @BeforeNowPlusXYears(years = -13, message = "You must be 13 years or older to register")
  @AfterNowPlusXYears(years = -100, message = "Must be less than 100 years old to register")
  private LocalDate dateOfBirth;

  @Length(max = 25, message = "Phone Number has to be less than or equal to 25 Characters")
  private String phoneNumber;

  @NotBlank(message = "Password is Required")
  @Length(max = 60, message = "Password has to be less than or equal to 60 Characters")
  @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+).{8,}", message = "The password must be at least 8 characters long and contain lowercase, uppercase and number characters")
  private String password;

  @Valid
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
