package com.navbara_pigeons.wasteless.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.navbara_pigeons.wasteless.dto.BasicBusinessDto;
import com.navbara_pigeons.wasteless.dto.BasicUserDto;
import com.navbara_pigeons.wasteless.dto.CreateUserDto;
import com.navbara_pigeons.wasteless.dto.FullUserDto;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@Entity
@Table(name = "USER")
public class User {


  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ID")
  private long id;

  @Column(name = "FIRST_NAME")
  private String firstName;

  @Column(name = "LAST_NAME")
  private String lastName;

  @Column(name = "MIDDLE_NAME")
  private String middleName;

  @Column(name = "NICKNAME")
  private String nickname;

  @Column(name = "BIO")
  private String bio;

  @Column(name = "EMAIL")
  private String email;

  @Column(name = "DATE_OF_BIRTH")
  private LocalDate dateOfBirth;

  @Column(name = "PHONE_NUMBER")
  private String phoneNumber;

  @Column(name = "IMAGE_NAME")
  private String imageName;

  @Column(name = "CREATED")
  private ZonedDateTime created;

  @Column(name = "ROLE")
  private String role;


  @Column(name = "PASSWORD")
  private String password;

  @OneToOne(fetch = FetchType.EAGER, optional = false)
  @JoinColumn(name = "HOME_ADDRESS_ID", referencedColumnName = "ID")
  private Address homeAddress;

  @JsonIgnore
  @ToString.Exclude
  @ManyToMany(
      fetch = FetchType.LAZY,
      cascade = {
          CascadeType.DETACH,
          CascadeType.MERGE,
          CascadeType.PERSIST,
          CascadeType.REFRESH
      }
  )
  @JoinTable(
      name = "USER_BUSINESS",
      joinColumns = @JoinColumn(name = "USER_ID"),
      inverseJoinColumns = @JoinColumn(name = "BUSINESS_ID")
  )
  private List<Business> businesses = new ArrayList<>();

  public User(BasicUserDto user) {
    this.id = user.getId();
    this.firstName = user.getFirstName();
    this.lastName = user.getLastName();
    this.middleName = user.getMiddleName();
    this.nickname = user.getNickname();
    this.bio = user.getBio();
    this.homeAddress = new Address(user.getHomeAddress());
    this.created = user.getCreated();
    this.role = user.getRole();
    for (BasicBusinessDto business : user.getBusinesses()) {
      this.businesses.add(new Business(business));
    }
  }

  public User(FullUserDto user) {
    this.id = user.getId();
    this.firstName = user.getFirstName();
    this.lastName = user.getLastName();
    this.middleName = user.getMiddleName();
    this.nickname = user.getNickname();
    this.bio = user.getBio();
    this.email = user.getEmail();
    this.dateOfBirth = user.getDateOfBirth();
    this.phoneNumber = user.getPhoneNumber();
    this.homeAddress = new Address(user.getHomeAddress());
    this.created = user.getCreated();
    this.role = user.getRole();
    for (BasicBusinessDto business : user.getBusinesses()) {
      this.businesses.add(new Business(business));
    }
  }

  public User(CreateUserDto user) {
    this.firstName = user.getFirstName();
    this.lastName = user.getLastName();
    this.middleName = user.getMiddleName();
    this.nickname = user.getNickname();
    this.bio = user.getBio();
    this.email = user.getEmail();
    this.dateOfBirth = user.getDateOfBirth();
    this.phoneNumber = user.getPhoneNumber();
    this.password = user.getPassword();
    this.homeAddress = new Address(user.getHomeAddress());
  }

  public User() {

  }

  /**
   * This is a helper method to add a business to the user.
   *
   * @param business The business to add.
   */
  public void addBusiness(Business business) {
    if (this.businesses == null) {
      this.businesses = new ArrayList<>();
    }
    this.businesses.add(business);
  }

  public void revokeAdmin() {
    this.role = "ROLE_USER";
  }

  public void makeAdmin() {
    this.role = "ROLE_ADMIN";
  }

}
