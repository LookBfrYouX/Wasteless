package com.navbara_pigeons.wasteless.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.navbara_pigeons.wasteless.dto.BasicAddressDto;
import com.navbara_pigeons.wasteless.dto.FullAddressDto;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;


@Data
@Entity
@Table(name = "ADDRESS")
public class Address {

  @JsonIgnore
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ID")
  private long id;

  @Column(name = "STREET_NUMBER")
  private String streetNumber;

  @Column(name = "STREET_NAME")
  private String streetName;

  @Column(name = "POSTCODE")
  private String postcode;

  @Column(name = "CITY")
  private String city;

  @Column(name = "REGION")
  private String region;

  @Column(name = "COUNTRY")
  private String country;

  public Address(BasicAddressDto address) {
    this.city = address.getCity();
    this.region = address.getRegion();
    this.country = address.getCountry();
  }

  public Address(FullAddressDto address) {
    this.streetNumber = address.getStreetNumber();
    this.streetName = address.getStreetName();
    this.postcode = address.getPostcode();
    this.city = address.getCity();
    this.region = address.getRegion();
    this.country = address.getCountry();
  }

  public Address() {

  }

}
