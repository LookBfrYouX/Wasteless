package com.navbara_pigeons.wasteless.entity;

import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "LISTING")
public class Listing {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ID")
  private long id;

  @OneToOne()
  @JoinColumn(name = "INVENTORY_ID")
  private InventoryItem inventoryItem;

  @Column(name = "QUANTITY")
  private long quantity;

  @Column(name = "PRICE")
  private float price;

  @Column(name = "MORE_INFO")
  private String moreInfo;

  @Column(name = "CREATED")
  private LocalDate created;

  @Column(name = "CLOSES")
  private LocalDate closes;
}
