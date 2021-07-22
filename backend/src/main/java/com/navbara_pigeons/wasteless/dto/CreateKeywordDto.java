package com.navbara_pigeons.wasteless.dto;

import com.navbara_pigeons.wasteless.entity.Keyword;
import javax.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class CreateKeywordDto {

  @NotBlank(message = "Keyword is Required")
  @Length(max = 50, message = "Keyword has to be less than or equal to 50 Characters")
  private String name;

  public CreateKeywordDto(Keyword keyword) {
    this.name = keyword.getName();
  }
}
