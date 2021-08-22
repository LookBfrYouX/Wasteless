package com.navbara_pigeons.wasteless.model;

import com.navbara_pigeons.wasteless.entity.BusinessType;
import com.navbara_pigeons.wasteless.enums.ListingSearchKeys;
import com.navbara_pigeons.wasteless.enums.ListingSortByOption;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import com.navbara_pigeons.wasteless.exception.ListingValidationException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.naming.directory.InvalidAttributeValueException;

/**
 * This model class maps to the request parameters sent to the /listings/search endpoint.
 * It is necessary as SonarLint does not allow more than seven parameters per method.
 */

@Data
@Slf4j
public class ListingsSearchParams {

  private Integer pagStartIndex;
  private Integer pagEndIndex;
  private ListingSortByOption sortBy;
  private boolean isAscending;
  private List<ListingSearchKeys> searchKeys;
  private String searchParam;
  private Double minPrice;
  private Double maxPrice;
  private List<ZonedDateTime> filterDates;
  private List<BusinessType> businessTypes;

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
      this.pagEndIndex = 9;
    } else {
      this.pagEndIndex = pagEndIndex;
    }
  }


  public void setAscending(Boolean ascending) {
    if (ascending == null) {
      this.isAscending = true;
    } else {
      this.isAscending = ascending;
    }
  }

  public void setSearchKeys(List<String> searchKeys) throws ListingValidationException {
    this.searchKeys = new ArrayList<>();
    if (searchKeys == null || searchKeys.isEmpty()) {
      this.searchKeys.add(ListingSearchKeys.PRODUCT_NAME);
    } else {
      for (String key : searchKeys) {
        try {
          this.searchKeys.add(ListingSearchKeys.fromString(key));
        } catch (IllegalArgumentException e){
          throw new ListingValidationException();
        }
      }
    }

  }

  public void setFilterDates(List<ZonedDateTime> filterDates) {
    this.filterDates = new ArrayList<>();
    if (filterDates != null) {
      for (ZonedDateTime date : filterDates) {
        if (date != null) {
          this.filterDates.add(date);
        }
      }
    }
  }

  public void setBusinessTypes(List<BusinessType> businessTypes) {
    this.businessTypes = businessTypes;
  }

  public void setSortBy(ListingSortByOption sortBy) {
    if (sortBy == null) {
      this.sortBy = ListingSortByOption.valueOf("created");
    } else {
      this.sortBy = sortBy;
    }
  }
}
