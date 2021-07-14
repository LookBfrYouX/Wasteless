package com.navbara_pigeons.wasteless.dto;

import com.navbara_pigeons.wasteless.entity.Keyword;
import lombok.Data;

@Data
public class CreateKeywordDto {

  private String name;

  public CreateKeywordDto(Keyword keyword) {
    this.name = keyword.getName();
  }
}
