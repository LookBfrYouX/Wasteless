package com.navbara_pigeons.wasteless.entity;

import java.time.ZonedDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@Entity
@Table(name = "TRANSACTION")
public class Transaction {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ID")
  private Long id;

  @Column(name = "SALE_DATE")
  private ZonedDateTime saleDate;

  @Column(name = "LISTING_DATE")
  private ZonedDateTime listingDate;

  @OneToOne
  @JoinColumn(name = "PRODUCT_ID")
  private Product product;

  @Column(name = "AMOUNT")
  private Double amount;

}
