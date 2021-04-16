package com.navbara_pigeons.wasteless.entity;

import java.time.ZonedDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;

@Data
@Entity
@Table(name = "PRODUCT")
public class Product {

  @Id
  @Column(name = "ID")
  private String id;

  @Column(name = "NAME")
  private String name;

  @Column(name = "DESCRIPTION")
  private String description;

  @Column(name = "RRP")
  private Double rrp;

  @Column(name = "CREATED")
  private ZonedDateTime created;

}