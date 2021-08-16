package com.navbara_pigeons.wasteless.model;

import com.navbara_pigeons.wasteless.entity.BusinessType;
import com.navbara_pigeons.wasteless.enums.ListingSortByOption;
import java.time.LocalDate;
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
  private List<LocalDate> filterDates;
  private List<BusinessType> businessTypes;

}
