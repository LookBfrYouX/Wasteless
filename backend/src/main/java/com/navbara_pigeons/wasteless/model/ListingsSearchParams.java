package com.navbara_pigeons.wasteless.model;

import com.navbara_pigeons.wasteless.entity.BusinessType;
import com.navbara_pigeons.wasteless.enums.ListingSortByOption;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.Data;

/**
 * This model class maps to the request parameters sent to the /listings/search endpoint.
 * It is necessary as SonarLint does not allow more than seven parameters per method.
 */

@Data
public class ListingsSearchParams {

  private Integer pagStartIndex;
  private Integer pagEndIndex;
  private ListingSortByOption sortBy;
  private boolean isAscending;
  private List<String> searchKeys;
  private String searchParam;
  private Double minPrice;
  private Double maxPrice;
  private List<ZonedDateTime> filterDates;
  private List<BusinessType> businessTypes;

  public ListingsSearchParams() {
    // Setting defaults
    this.searchParam = "";
  }

  public void setSearchParam(String searchParam) {
    if (searchParam == null) {
      this.searchParam = "";
    } else {
      this.searchParam = searchParam;
    }
  }

  public void setPagStartIndex(Integer pagStartIndex) {
    if (pagStartIndex == null) {
      this.pagStartIndex = 0;
    } else {
      this.pagStartIndex = pagStartIndex;
    }
  }

  public void setPagEndIndex(Integer pagEndIndex) {
    if (pagEndIndex == null) {
      this.pagEndIndex = 10;
    } else {
      this.pagEndIndex = pagEndIndex;
    }
  }


  public void setAscending(boolean ascending) {
    if (ascending != true && ascending != false) {
      this.isAscending = true;
    } else {
      this.isAscending = ascending;
    }
  }

  public void setSearchKeys(List<String> searchKeys) {
    if (searchKeys.contains("Product Name") || searchKeys.contains("Address") || searchKeys.contains("Business Name")) {
      this.searchKeys = searchKeys;
    } else {
      this.searchKeys.add("Product Name");
    }
  }

  public void setFilterDates(List<ZonedDateTime> filterDates) {
    this.filterDates = filterDates;
  }

  public void setBusinessTypes(List<BusinessType> businessTypes) {
    System.out.println(businessTypes);
    this.businessTypes = businessTypes;
  }
}
