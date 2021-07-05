package com.navbara_pigeons.wasteless.helper;

import java.lang.reflect.Field;
import lombok.Getter;

@Getter
public class PaginationBuilder {

  Object entity;
  Integer pagStartIndex = 0;
  Integer pagEndIndex;
  String sortField;
  boolean sortAscending = true;

  public PaginationBuilder(Object entity, String defaultSortField) {
    this.entity = entity;
    // Use the parse method as it checks if the sort field exists in the entity
    if (defaultSortField != null) {
      parseSortByString(defaultSortField + "-acs");
    }
  }

  public PaginationBuilder withSortByString(String sortByString) {
    if (sortByString != null) {
      parseSortByString(sortByString);
    }
    return this;
  }

  public PaginationBuilder withPagStartIndex(Integer pagStartIndex) {
    if (pagStartIndex != null) {
      if (pagStartIndex < 0) {
        throw new IllegalArgumentException(
            "The pagination start index must be greater than zero");
      }
      if (this.pagEndIndex != null && pagStartIndex > this.pagEndIndex) {
        throw new IllegalArgumentException(
            "The pagination start index must be smaller or equal to the pagination end index");
      }
      this.pagStartIndex = pagStartIndex;
    }
    return this;
  }

  public PaginationBuilder withPagEndIndex(Integer pagEndIndex) {
    if (pagEndIndex != null) {
      if (this.pagStartIndex > pagEndIndex) {
        throw new IllegalArgumentException(
            "The pagination start index must be smaller or equal to the pagination end index");
      }
      this.pagEndIndex = pagEndIndex;
    }
    return this;
  }

  public PaginationBuilder withSortField(String sortField) {
    this.sortField = sortField;
    return this;
  }

  public PaginationBuilder withSortAscending(boolean sortAscending) {
    this.sortAscending = sortAscending;
    return this;
  }

  private void parseSortByString(String sortByString) {
    String[] splitSortBy = sortByString.split("-");

    if (splitSortBy.length != 2) {
      throw new IllegalArgumentException(
          "SortByString must be in 'entityFieldName-<acs/desc>' format");
    }

    String passedSortField = splitSortBy[0];
    String passedSortAscending = splitSortBy[1];

    boolean validField = false;
    for (Field field : ((Class) entity).getDeclaredFields()) {
      if (field.getName().equals(passedSortField)) {
        this.sortField = field.getName();
        validField = true;
        break;
      }
    }

    if (!validField) {
      throw new IllegalArgumentException(
          "The passed in field to sort by does not exist in the " + ((Class) this.entity).getName()
              + " class");
    }

    if (passedSortAscending.equals("desc")) {
      this.sortAscending = false;
    }
  }

}
