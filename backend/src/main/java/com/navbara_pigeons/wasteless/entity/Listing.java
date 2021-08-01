package com.navbara_pigeons.wasteless.entity;

import com.navbara_pigeons.wasteless.dto.CreateListingDto;
import java.time.ZonedDateTime;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import lombok.Data;

@Data
@Entity
@Table(name = "LISTING")
public class Listing {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ID")
  private long id;

  @ManyToOne(
      fetch = FetchType.LAZY,
      cascade = {
          CascadeType.DETACH,
          CascadeType.MERGE,
          CascadeType.PERSIST,
          CascadeType.REFRESH
      }
  )
  @JoinColumn(name = "INVENTORY_ITEM_ID")
  private InventoryItem inventoryItem;

  @Column(name = "QUANTITY")
  private long quantity;

  @DecimalMin(message = "price must be above 0.01", value = "0.01")
  @DecimalMax(message = "price must be below 10,000,000", value = "10000000.00")
  @Column(name = "PRICE")
  private Double price;

  @Column(name = "MORE_INFO")
  private String moreInfo;

  @Column(name = "CREATED")
  private ZonedDateTime created;

  @Column(name = "CLOSES")
  private ZonedDateTime closes;

  public Listing(CreateListingDto createListingDto) {
    this.quantity = createListingDto.getQuantity();
    this.price = createListingDto.getPrice();
    this.moreInfo = createListingDto.getMoreInfo();
    this.closes = createListingDto.getCloses();
  }

  public Listing() {

  }
}
