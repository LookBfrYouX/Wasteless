package com.navbara_pigeons.wasteless.entity;

import com.navbara_pigeons.wasteless.dto.CreateMarketListingDto;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Data
@ToString
@Entity
@Table(name = "MARKETLISTING")
public class MarketListing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
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

    public MarketListing(CreateMarketListingDto marketListingDto, User user) {
        this.creator = user;
        this.section = marketListingDto.getSection();
        this.title = marketListingDto.getTitle();
        this.description = marketListingDto.getDescription();
    }

}
