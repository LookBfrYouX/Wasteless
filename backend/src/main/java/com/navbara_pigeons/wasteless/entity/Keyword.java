package com.navbara_pigeons.wasteless.entity;

import com.navbara_pigeons.wasteless.dto.CreateKeywordDto;
import java.time.ZonedDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

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
