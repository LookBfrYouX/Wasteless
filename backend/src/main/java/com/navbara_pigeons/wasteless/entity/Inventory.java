package com.navbara_pigeons.wasteless.entity;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.*;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "INVENTORY")
public class Inventory {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ID")
  private long id;

  @OneToOne()
  @JoinColumn(name = "PRODUCT_ID")
  private Product product;

  @OneToOne()
  @JoinColumn(name = "BUSINESS_ID")
  private Business business;

  @Column(name = "QUANTITY")
  private long quantity;

  @Column(name = "PRICE_PER_ITEM")
  private float price;

  @Column(name = "TOTAL_PRICE")
  private float totalPrice;

  @Column(name = "EXPIRES")
  private LocalDate expires;

  @Column(name = "MANUFACTURED")
  private LocalDate manufactured;

  @Column(name = "SELL_BY")
  private LocalDate sellBy;

  @Column(name = "BEST_BEFORE")
  private LocalDate bestBefore;

  @JsonIgnore
  @OneToMany(
    fetch = FetchType.LAZY,
    cascade = {
        CascadeType.DETACH,
        CascadeType.MERGE,
        CascadeType.PERSIST,
        CascadeType.REFRESH
    }
  )
  @JoinColumn(name = "INVENTORY_ID")
  private List<Listing> listings;

  /**
   * Helper method which adds a listing to a inventory item
   * @param listing
   */
  public void addListing(Listing listing) {
      if (listings == null) {
          listings = new ArrayList<>();
      }
      listings.add(listing);
  } 
}
