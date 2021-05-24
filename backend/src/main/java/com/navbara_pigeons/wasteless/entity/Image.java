package com.navbara_pigeons.wasteless.entity;

import com.navbara_pigeons.wasteless.dto.BasicImageDto;
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
  private long id;

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

  public Image(BasicImageDto image) {
    // TODO publicPathPrefix gets added which will break it if the publicPathPrefix is not /
    this.id = image.getId();
    this.path = image.getFilename();
    this.thumbnailPath = image.getThumbnailFilename();
  }

  /**
   * A required default constructor
   */
  public Image() {
    ;
  }
}
