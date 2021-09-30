package com.navbara_pigeons.wasteless.model;

import com.navbara_pigeons.wasteless.entity.BusinessType;
import com.navbara_pigeons.wasteless.enums.ListingSearchKeys;
import com.navbara_pigeons.wasteless.enums.ListingSortByOption;
import com.navbara_pigeons.wasteless.enums.NutriScore;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Range;

/**
 * This model class maps to the request parameters sent to the /listings/search endpoint. It is
 * necessary as SonarLint does not allow more than seven parameters per method.
 */

@Data
@Slf4j
public class ListingsSearchParams {

  // Searching, Pagination & Sorting Params
  private Integer pagStartIndex;
  private Integer pagEndIndex;
  // returns all listings by default
  private int defaultPageEndIndex = 1000000000;
  private ListingSortByOption sortBy;
  private boolean isAscending;
  private List<ListingSearchKeys> searchKeys;
  private String searchParam;

  // Product Detail Params
  private Double minPrice;
  private Double maxPrice;
  private List<ZonedDateTime> filterDates;
  private List<BusinessType> businessTypes;

  // Nutrition Filtering Params
  private NutriScore minNutriScore;
  private NutriScore maxNutriScore;
  private NutriScore nutriScore;
  @Range(min=1, max=4)
  private Integer minNovaGroup;
  @Range(min=1, max=4)
  private Integer maxNovaGroup;


  /**
   * setting default value for search string so returns all values if user searches for nothing,
   * acts as a setter otherwise
   *
   * @param searchParam String value to set search param
   */
  public void setSearchParam(String searchParam) {
    if (searchParam == null) {
      this.searchParam = "";
    } else {
      this.searchParam = searchParam;
    }
  }

  /**
   * setting default value for page start index if given null, acts as a setter otherwise
   *
   * @param pagStartIndex Integer value to set pagStartIndex
   */
  public void setPagStartIndex(Integer pagStartIndex) {
    if (pagStartIndex == null) {
      this.pagStartIndex = 0;
    } else {
      this.pagStartIndex = pagStartIndex;
    }
  }

  /**
   * setting default value for page start index if given null, acts as a setter otherwise
   *
   * @param pagEndIndex Integer value to set pagEndIndex
   */
  public void setPagEndIndex(Integer pagEndIndex) {
    if (pagEndIndex == null) {
      this.pagEndIndex = defaultPageEndIndex;
    } else {
      this.pagEndIndex = pagEndIndex;
    }
  }

  /**
   * setting default value for isAscending property if given null, acts as a setter otherwise
   *
   * @param ascending Boolean value to set ascending
   */
  public void setAscending(Boolean ascending) {
    if (ascending == null) {
      this.isAscending = true;
    } else {
      this.isAscending = ascending;
    }
  }

  public void setSearchKeys(List<ListingSearchKeys> searchKeys) {
    if (searchKeys == null || searchKeys.isEmpty()) {
      this.searchKeys = new ArrayList<>();
      this.searchKeys.add(ListingSearchKeys.PRODUCT_NAME);
    } else {
      this.searchKeys = searchKeys;
    }

  }

  /**
   * setting default values for filtering dates if given null acts as a setter otherwise
   *
   * @param filterDates List<ZonedDateTime> value to set filterDates
   */
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

  /**
   * setter for business types property
   *
   * @param businessTypes List<ZonedDateTime> value to set businessTypes
   */
  public void setBusinessTypes(List<BusinessType> businessTypes) {
    this.businessTypes = businessTypes;
  }

  /**
   * Sets default value for sortBy to created if given null, acts as setter otherwise
   *
   * @param sortBy ListingSortByOption value to set sortBy
   */
  public void setSortBy(ListingSortByOption sortBy) {
    if (sortBy == null) {
      this.sortBy = ListingSortByOption.CREATED;
    } else {
      this.sortBy = sortBy;
    }
  }
}
