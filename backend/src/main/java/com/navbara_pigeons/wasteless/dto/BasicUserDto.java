package com.navbara_pigeons.wasteless.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.navbara_pigeons.wasteless.entity.Business;
import com.navbara_pigeons.wasteless.entity.User;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * User DTO which does not return coarse address, email, date of birth and phone number
 */
@Data
public class BasicUserDto {

    private long id;
    private String firstName;
    private String lastName;
    private String middleName;
    private String nickname;
    private String bio;
    private BasicAddressDto homeAddress;
    private ZonedDateTime created;
    private String role;
    @JsonProperty("businessesAdministered")
    private List<BasicBusinessDto> businesses;

    public BasicUserDto(User user) {
        this.id = user.getId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.middleName = user.getMiddleName();
        this.nickname = user.getNickname();
        this.bio = user.getBio();
        this.created = user.getCreated();
        this.role = user.getRole();
        this.homeAddress = new BasicAddressDto(user.getHomeAddress());
        if (user.getBusinesses() != null) {
            this.businesses = makeBusinessDto(user.getBusinesses());
        }
    }

    private List<BasicBusinessDto> makeBusinessDto(List<Business> businesses) {
        ArrayList<BasicBusinessDto> businessDtos = new ArrayList<BasicBusinessDto>();
        for (Business business : businesses) {
            businessDtos.add(new BasicBusinessDto(business));
        }
        return businessDtos;
    }

}
