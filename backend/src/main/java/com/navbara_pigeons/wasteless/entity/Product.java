package com.navbara_pigeons.wasteless.entity;

import java.time.ZonedDateTime;
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
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "PRODUCT")
public class Product {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ID")
  private long id;

  @Column(name = "NAME")
  private String name;

  @Column(name = "DESCRIPTION")
  private String description;

  @Column(name = "MANUFACTURER")
  private String manufacturer;

  @Column(name = "RRP")
  private Double recommendedRetailPrice;

  @Column(name = "CREATED")
  private ZonedDateTime created;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "PRIMARY_IMAGE_ID", referencedColumnName = "ID")
  private Image primaryProductImage;

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
      name = "PRODUCT_IMAGE",
      joinColumns = @JoinColumn(name = "PRODUCT_ID"),
      inverseJoinColumns = @JoinColumn(name = "IMAGE_ID")
  )
  private List<Image> productImages;

  /**
   * This is a helper method for adding a image to the product.
   *
   * @param image The product to be added.
   */
  public void addProductImage (Image image) {
    if (this.productImages == null) {
      this.productImages = new ArrayList<>();
    }
    this.productImages.add(image);
  }
}