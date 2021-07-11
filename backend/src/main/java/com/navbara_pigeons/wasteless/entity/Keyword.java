package com.navbara_pigeons.wasteless.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Data
@Entity
@Table(name = "KEYWORD")
public class Keyword {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private long id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "CREATED")
    private ZonedDateTime created;

    public Keyword(String name, ZonedDateTime created) {
        this.name = name;
        this.created = created;
    }

    public Keyword() {

    }
}
