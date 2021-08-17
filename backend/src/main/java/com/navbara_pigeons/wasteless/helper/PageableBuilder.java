package com.navbara_pigeons.wasteless.helper;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PageableBuilder {

  public static Pageable makePageable(int pagStartIndex, int pagEndIndex, String sortBy, boolean isAscending) {
    int itemsPerPage = pagEndIndex - pagStartIndex;
    int pageNumber = (pagStartIndex / itemsPerPage);
    System.out.println("PAGEABLE: " + itemsPerPage + " : " + pageNumber + " : " + sortBy + " : " + isAscending);
    if (isAscending) {
      return PageRequest.of(pageNumber, itemsPerPage, Sort.by(sortBy).ascending());
    } else {
      return PageRequest.of(pageNumber, itemsPerPage, Sort.by(sortBy).descending());
    }
  }

}
