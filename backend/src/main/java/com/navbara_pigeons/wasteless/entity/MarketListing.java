package com.navbara_pigeons.wasteless.entity;

import com.navbara_pigeons.wasteless.dto.CreateMarketListingDto;
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
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@Entity
@Table(name = "MARKETLISTING")
public class MarketListing {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ID")
  private Long id;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "CREATOR_ID")
  private User creator;

  @Column(name = "SECTION")
  private String section;

  @Column(name = "CREATED")
  private ZonedDateTime created;

  @Column(name = "DISPLAY_PERIOD_END")
  private ZonedDateTime displayPeriodEnd;

  @Column(name = "TITLE")
  private String title;

  @Column(name = "DESCRIPTION")
  private String description;

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
      name = "MARKETLISTING_KEYWORD",
      joinColumns = @JoinColumn(name = "MARKETLISTING_ID"),
      inverseJoinColumns = @JoinColumn(name = "KEYWORD_ID")
  )
  private List<Keyword> keywords;

  public MarketListing() {

  }

  public MarketListing(CreateMarketListingDto marketListingDto, User user) {
    this.creator = user;
    this.section = marketListingDto.getSection();
    this.title = marketListingDto.getTitle();
    this.description = marketListingDto.getDescription();
    // Converting the Keyword Id's into Keywords was not in the scope of task SENG302T300-207
    // This must be completed in task SENG302T300-229, possible create a method in the MarketListing
    //   service that can convert a list of Id's into the entities
    //    this.keywords =
  }

  /**
   * Add a keyword to market listing.
   * @param keyword
   */
  public void addKeyword(Keyword keyword) {
    if (this.keywords == null) {
      this.keywords = new ArrayList<>();
    }
    this.keywords.add(keyword);
  }
}
