package com.navbara_pigeons.wasteless.helper;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * Override of Pageable that supports arbitrary start/end indexes
 * Does not support:
 *
 * - getPageNumber()
 * - next()
 * - previousOrFirst()
 * - first()
 * - hasPrevious()
 */
public class PageableBuilder implements Pageable {

  private final int pagStartIndex;
  private final int pagEndIndex;
  private final String sortBy;
  private final boolean isAscending;

  /**
   * default constructor
   * @param pagStartIndex start of page is inclusive
   * @param pagEndIndex end of page is inclusive
   * @param sortBy
   * @param isAscending
   */
  public PageableBuilder(int pagStartIndex, int pagEndIndex, String sortBy, boolean isAscending) {
    this.pagStartIndex = pagStartIndex;
    this.pagEndIndex = pagEndIndex;
    this.sortBy = sortBy;
    this.isAscending = isAscending;
  }

  @Override
  public int getPageNumber() {
    return 0;
  }

  /**
   * getter for page size
   * @return page size
   */
  @Override
  public int getPageSize() {
    return pagEndIndex - pagStartIndex + 1;
  }

  /**
   * gets page start index
   * @return page start index
   */
  @Override
  public long getOffset() {
    return pagStartIndex;
  }

  /**
   * gets sort by field
   * @return sort by field
   */
  @Override
  public Sort getSort() {
    Sort sort = Sort.by(this.sortBy);
    if (sortBy == "name") {
       sort = Sort.by("inventoryItem.product.id");
    }

    if (isAscending) {
      return sort.ascending();
    } else {
      return sort.descending();
    }
  }

  @Override
  public Pageable next() {
    return null;
  }

  @Override
  public Pageable previousOrFirst() {
    return null;
  }

  @Override
  public Pageable first() {
    return null;
  }

  @Override
  public boolean hasPrevious() {
    return false;
  }
}
