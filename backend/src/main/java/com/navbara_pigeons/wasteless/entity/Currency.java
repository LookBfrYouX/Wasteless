package com.navbara_pigeons.wasteless.entity;

import java.io.Serializable;
import lombok.Data;

/**
 * Class for currency from restcountries API. NOT stored in DB or managed by Spring
 */
@Data
public class Currency implements Serializable {

  private String code; // Use as ID
  private String name;
  private String symbol;

  public Currency() {
  }

  public Currency(String code, String name, String symbol) {
    this.code = code;
    this.name = name;
    this.symbol = symbol;
  }

  @Override
  public String toString() {
    return String.format("%s (%s, %s)", name, code, symbol);
  }
}
