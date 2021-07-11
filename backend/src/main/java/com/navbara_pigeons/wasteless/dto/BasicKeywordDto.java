package com.navbara_pigeons.wasteless.dto;

import com.navbara_pigeons.wasteless.entity.Keyword;
import lombok.Data;

@Data
public class BasicKeywordDto {

  private String name;

  public BasicKeywordDto(Keyword keyword) {
    this.name = keyword.getName();
  }
}
