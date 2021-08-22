package com.navbara_pigeons.wasteless.helper;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PageableBuilder {

  private PageableBuilder() {}

  public static Pageable makePageable(int pagStartIndex, int pagEndIndex, String sortBy, boolean isAscending) {
    int itemsPerPage = pagEndIndex - pagStartIndex;
    int pageNumber = (pagStartIndex / itemsPerPage);
    if (sortBy == "name")
      sortBy = "inventoryItem.product.id";
    if (isAscending) {
      return PageRequest.of(pageNumber, itemsPerPage, Sort.by(sortBy).ascending());
    } else {
      return PageRequest.of(pageNumber, itemsPerPage, Sort.by(sortBy).descending());
    }
  }
}
