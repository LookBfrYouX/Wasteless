package com.navbara_pigeons.wasteless.dto;

import java.util.List;
import lombok.Data;

@Data
public class PaginationDto<T> {

  private List<T> results;
  private Long totalCount;

  public PaginationDto(List<T> results, Long totalCount) {
    this.results = results;
    this.totalCount = totalCount;
  }

  public PaginationDto() {
  }
}
