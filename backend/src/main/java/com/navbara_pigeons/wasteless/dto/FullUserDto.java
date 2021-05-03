package com.navbara_pigeons.wasteless.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.navbara_pigeons.wasteless.entity.Business;
import com.navbara_pigeons.wasteless.entity.User;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class FullUserDto {

    private long id;
    private String firstName;
    private String lastName;
    private String middleName;
    private String nickname;
    private String bio;
    private String email;
    private String dateOfBirth;
    private String phoneNumber;
    private BasicAddressDto homeAddress;
//    private String imageName;
    private ZonedDateTime created;
    private String role;
//    private String password;
    @JsonProperty("businessesAdministered")
    private List<BasicBusinessDto> businesses;

    public FullUserDto(User user) {
        this.id = user.getId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.middleName = user.getMiddleName();
        this.nickname = user.getNickname();
        this.bio = user.getBio();
        this.email = user.getEmail();
        this.dateOfBirth = user.getDateOfBirth();
        this.phoneNumber = user.getPhoneNumber();
//        this.imageName = user.getImageName();
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
