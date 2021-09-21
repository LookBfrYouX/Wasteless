package com.navbara_pigeons.wasteless.dto;

import java.util.List;
import lombok.Data;

/**
 * User DTO which returns all sales report query
 */
@Data
public class FullSalesReportDto {

  private List<Object> results;
  private Long totalNumber;
  private Long totalValue;

  public FullSalesReportDto(List<Object> results, Long totalNumber, Long totalValue) {
    this.results = results;
    this.totalNumber = totalNumber;
    this.totalValue = totalValue;
  }

}
