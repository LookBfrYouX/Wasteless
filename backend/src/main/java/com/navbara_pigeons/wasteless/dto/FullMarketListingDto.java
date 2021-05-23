package com.navbara_pigeons.wasteless.dto;

import com.navbara_pigeons.wasteless.entity.User;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class FullMarketListingDto {

    private Long id;
    private FullUserDto creator;
    private String section;
    private ZonedDateTime created;
    private ZonedDateTime displayPeriodEnd;
    private String title;
    private String description;

}
