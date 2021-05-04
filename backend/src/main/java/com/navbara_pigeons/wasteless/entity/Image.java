package com.navbara_pigeons.wasteless.entity;

import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "IMAGE")
public class Image {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ID")
  private int id;

  @Column(name = "FILENAME")
  private String path;

  @Column(name = "THUMBNAIL_FILENAME")
  private String thumbnailPath;

  /**
   * Constructor for the image entity
   *
   * @param extension The Extension of the filename
   */
  public Image(String imageDirectory, String extension) {
    String filename = "I" + UUID.randomUUID();
    this.path = imageDirectory + filename + "." + extension;
    this.thumbnailPath = imageDirectory + filename + "_thumbnail." + extension;
  }

  /**
   * A required default constructor
   */
  public Image() {
    ;
  }
}
