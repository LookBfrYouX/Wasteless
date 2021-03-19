package com.pigeon.wasteless.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
  private String dateOfBirth;

  @Column(name = "PHONE_NUMBER")
  private String phoneNumber;

  @Column(name = "HOME_ADDRESS")
  private String homeAddress;

  @Column(name = "CREATED")
  private String created;

  @Column(name = "ROLE")
  private String role;

  @Column(name = "PASSWORD")
  private String password;

  @JsonIgnore
  @ToString.Exclude
  @ManyToMany(
      fetch = FetchType.EAGER,
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
  private List<Business> businesses;

  /**
   * This is a helper method to add a business to the user.
   *
   * @param business The business to add.
   */
  public void addBusiness(Business business) {
    if (this.businesses == null) {
      this.businesses = new ArrayList<Business>();
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
