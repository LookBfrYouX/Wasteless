package com.navbara_pigeons.wasteless.entity;

import com.navbara_pigeons.wasteless.dto.BasicInventoryItemDto;
import com.navbara_pigeons.wasteless.dto.BasicInventoryDto;
import com.navbara_pigeons.wasteless.dto.CreateInventoryItemDto;
import com.navbara_pigeons.wasteless.dto.FullInventoryDto;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "INVENTORY_ITEM")
public class InventoryItem {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ID")
  private long id;

  @OneToOne()
  @JoinColumn(name = "PRODUCT_ID")
  private Product product;

  @JsonIgnore
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "BUSINESS_ID")
  private Business business;

  @Column(name = "QUANTITY")
  private long quantity;

  @Column(name = "PRICE_PER_ITEM")
  private Double pricePerItem;

  @Column(name = "TOTAL_PRICE")
  private Double totalPrice;

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
  @JoinColumn(name = "INVENTORY_ITEM_ID")
  private List<Listing> listings;

  public InventoryItem(BasicInventoryItemDto inventory) {
    this.id = inventory.getId();
    this.product = new Product(inventory.getProduct());
    this.quantity = inventory.getQuantity();
    this.pricePerItem = inventory.getPricePerItem();
    this.totalPrice = inventory.getTotalPrice();
    this.expires = inventory.getExpires();
    this.manufactured = inventory.getManufactured();
    this.sellBy = inventory.getSellBy();
    this.bestBefore = inventory.getBestBefore();
  }

  public InventoryItem(FullInventoryDto inventory) {
    this.product = new Product(inventory.getProduct());
    this.quantity = inventory.getQuantity();
    this.pricePerItem = inventory.getPrice();
    this.totalPrice = inventory.getTotalPrice();
    this.expires = inventory.getExpires();
    this.manufactured = inventory.getManufactured();
    this.sellBy = inventory.getSellBy();
    this.bestBefore = inventory.getBestBefore();
  }

  public InventoryItem(CreateInventoryItemDto inventory) {
    this.quantity = inventory.getQuantity();
    this.pricePerItem = inventory.getPricePerItem();
    this.totalPrice = inventory.getTotalPrice();
    this.expires = inventory.getExpires();
    this.manufactured = inventory.getManufactured();
    this.sellBy = inventory.getSellBy();
    this.bestBefore = inventory.getBestBefore();
  }

  public InventoryItem() {
  }

  /**
   * Helper method which adds a listing to a inventory item
   *
   * @param listing
   */
  public void addListing(Listing listing) {
    if (listings == null) {
      listings = new ArrayList<>();
    }
    listings.add(listing);
  }
}
