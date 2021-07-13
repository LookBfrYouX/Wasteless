package com.navbara_pigeons.wasteless.dto;

import java.util.List;
import lombok.Data;

@Data
public class PaginationDto<T> {

  private List<T> data;
  private Long totalCount;

  public PaginationDto(List<T> data, Long totalCount) {
    this.data = data;
    this.totalCount = totalCount;
  }

  public PaginationDto() {}
}
