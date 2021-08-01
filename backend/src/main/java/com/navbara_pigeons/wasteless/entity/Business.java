package com.navbara_pigeons.wasteless.entity;

import com.navbara_pigeons.wasteless.dto.BasicBusinessDto;
import com.navbara_pigeons.wasteless.dto.BasicUserDto;
import com.navbara_pigeons.wasteless.dto.CreateBusinessDto;
import com.navbara_pigeons.wasteless.dto.FullBusinessDto;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "BUSINESS")
public class Business {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ID")
  private long id;

  @Column(name = "PRIMARY_ADMINISTRATOR_ID")
  private Long primaryAdministratorId;

  @Column(name = "NAME")
  private String name;

  @Column(name = "DESCRIPTION")
  private String description;

  @OneToOne(fetch = FetchType.EAGER, optional = false)
  @JoinColumn(name = "ADDRESS_ID", referencedColumnName = "ID")
  private Address address;

  @Column(name = "BUSINESS_TYPE")
  @Enumerated(EnumType.STRING)
  private BusinessType businessType;

  @Column(name = "CREATED")
  private ZonedDateTime created;

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
      joinColumns = @JoinColumn(name = "BUSINESS_ID"),
      inverseJoinColumns = @JoinColumn(name = "USER_ID")
  )
  private List<User> administrators = new ArrayList<>();

  @OneToMany(
      fetch = FetchType.LAZY,
      cascade = {
          CascadeType.DETACH,
          CascadeType.MERGE,
          CascadeType.PERSIST,
          CascadeType.REFRESH
      }
  )
  @JoinTable(
      name = "CATALOGUE",
      joinColumns = @JoinColumn(name = "BUSINESS_ID"),
      inverseJoinColumns = @JoinColumn(name = "PRODUCT_ID")
  )
  private List<Product> productsCatalogue = new ArrayList<>();

  @OneToMany(
      fetch = FetchType.LAZY,
      cascade = {
          CascadeType.DETACH,
          CascadeType.MERGE,
          CascadeType.PERSIST,
          CascadeType.REFRESH
      }
  )
  @JoinColumn(name = "BUSINESS_ID")
  private List<InventoryItem> inventory = new ArrayList<>();

  public Business(FullBusinessDto business) {
    this.id = business.getId();
    this.primaryAdministratorId = business.getPrimaryAdministratorId();
    this.name = business.getName();
    this.description = business.getDescription();
    this.address = new Address(business.getAddress());
    this.businessType = BusinessType.fromString(business.getBusinessType());
    this.created = business.getCreated();
    for (BasicUserDto userDto : business.getAdministrators()) {
      this.administrators.add(new User(userDto));
    }
  }

  public Business(BasicBusinessDto business) {
    this.id = business.getId();
    this.primaryAdministratorId = business.getPrimaryAdministratorId();
    this.name = business.getName();
    this.description = business.getDescription();
    this.address = new Address(business.getAddress());
    this.businessType = BusinessType.fromString(business.getBusinessType());
    this.created = business.getCreated();
  }

  public Business(CreateBusinessDto business) {
    this.primaryAdministratorId = business.getPrimaryAdministratorId();
    this.name = business.getName();
    this.description = business.getDescription();
    this.address = new Address(business.getAddress());
    this.businessType = BusinessType.fromString(business.getBusinessType());
  }

  public Business() {
  }

  /**
   * This is a helper method for adding a user to the business.
   *
   * @param administrator The user to be added.
   */
  public void addAdministrator(User administrator) {
    if (this.administrators == null) {
      this.administrators = new ArrayList<>();
    }
    this.administrators.add(administrator);
  }

  /**
   * This is a helper method for adding a user to the business.
   *
   * @param administrator The user to be added.
   */
  public void removeAdministrator(User administrator) {
    this.administrators.remove(administrator);
  }

  /**
   * This is a helper method for adding a product to the business product catalogue.
   *
   * @param product The product to be added.
   */
  public void addCatalogueProduct(Product product) {
    if (this.productsCatalogue == null) {
      this.productsCatalogue = new ArrayList<>();
    }
    this.productsCatalogue.add(product);
  }

  /**
   * This is a helper method for adding an inventory item to the business inventory.
   *
   * @param inventoryItem The product to be added.
   */
  public void addInventoryItem(InventoryItem inventoryItem) {
    if (this.inventory == null) {
      this.inventory = new ArrayList<>();
    }
    this.inventory.add(inventoryItem);
  }
}
