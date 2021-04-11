package com.navbara_pigeons.wasteless.entity;

import javax.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "ADDRESS")
public class Address {

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
}
