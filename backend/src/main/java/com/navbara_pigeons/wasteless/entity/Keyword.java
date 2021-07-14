package com.navbara_pigeons.wasteless.entity;

import com.navbara_pigeons.wasteless.dto.CreateKeywordDto;
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

  public Keyword(CreateKeywordDto keywordDto) {
    this.name = keywordDto.getName();
  }

  public Keyword() {

  }
}
