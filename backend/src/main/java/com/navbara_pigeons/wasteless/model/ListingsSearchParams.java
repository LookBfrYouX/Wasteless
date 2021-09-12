package com.navbara_pigeons.wasteless.model;

import com.navbara_pigeons.wasteless.entity.BusinessType;
import com.navbara_pigeons.wasteless.enums.ListingSearchKeys;
import com.navbara_pigeons.wasteless.enums.ListingSortByOption;
import com.navbara_pigeons.wasteless.enums.NutritionFactsLevel;

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

  private List<NutritionFactsLevel> fat;
  private List<NutritionFactsLevel> saturatedFat;
  private List<NutritionFactsLevel> sugars;
  private List<NutritionFactsLevel> salt;

  private int DEFAULT_PAGE_END_INDEX = 1000000000;

  /**
  * setting default value for search string so returns all values if user searches for nothing, acts as a setter otherwise
   * @param searchParam
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
   * @param pagStartIndex
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
   * @param pagEndIndex
   */
  public void setPagEndIndex(Integer pagEndIndex) {
    if (pagEndIndex == null) {
      this.pagEndIndex = DEFAULT_PAGE_END_INDEX;
    } else {
      this.pagEndIndex = pagEndIndex;
    }
  }

  /**
   * setting default value for isAscending property if given null, acts as a setter otherwise
   * @param ascending
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
   * @param filterDates
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
   * @param businessTypes
   */
  public void setBusinessTypes(List<BusinessType> businessTypes) {
    this.businessTypes = businessTypes;
  }

  /**
   * Sets default value for sortBy to created if given null, acts as setter otherwise
   * @param sortBy
   */
  public void setSortBy(ListingSortByOption sortBy) {
    if (sortBy == null) {
      this.sortBy = ListingSortByOption.valueOf("created");
    } else {
      this.sortBy = sortBy;
    }
  }

  /**
   * Sets the allowed fat levels. null means the filter will not be applied
   * @param fat fat levels. If the array is empty it is set to null
   */
  public void setFat(List<NutritionFactsLevel> fat) {
    if (fat == null || fat.isEmpty()) {
      this.fat = null;
    } else {
      this.fat = fat;
    }
  }


  /**
   * Sets the allowed saturated fat levels. null means the filter will not be applied
   * @param saturatedFat saturatedFat levels. If the array is empty it is set to null
   */
  public void setSaturatedFat(List<NutritionFactsLevel> saturatedFat) {
    if (saturatedFat == null || saturatedFat.isEmpty()) {
      this.saturatedFat = null;
    } else {
      this.saturatedFat = saturatedFat;
    }
  }


  /**
   * Sets the allowed sugar levels. null means the filter will not be applied
   * @param sugars sugar levels. If the array is empty it is set to null
   */
  public void setSugars(List<NutritionFactsLevel> sugars) {
    if (sugars == null || sugars.isEmpty()) {
      this.sugars = null;
    } else {
      this.sugars = sugars;
    }
  }


  /**
   * Sets the allowed salt levels. null means the filter will not be applied
   * @param salt salt levels. If the array is empty it is set to null
   */
  public void setSalt(List<NutritionFactsLevel> salt) {
    if (salt == null || salt.isEmpty()) {
      this.salt = null;
    } else {
      this.salt = salt;
    }
  }
}
