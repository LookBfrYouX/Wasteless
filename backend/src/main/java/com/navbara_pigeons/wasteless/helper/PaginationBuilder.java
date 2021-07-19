package com.navbara_pigeons.wasteless.helper;

import static com.navbara_pigeons.wasteless.enums.UserSortByOption.FIRST_NAME;

import com.navbara_pigeons.wasteless.entity.InventoryItem;
import com.navbara_pigeons.wasteless.entity.Listing;
import com.navbara_pigeons.wasteless.entity.MarketListing;
import com.navbara_pigeons.wasteless.entity.Product;
import com.navbara_pigeons.wasteless.entity.User;
import com.navbara_pigeons.wasteless.enums.InventorySortByOption;
import com.navbara_pigeons.wasteless.enums.ListingSortByOption;
import com.navbara_pigeons.wasteless.enums.MarketListingSortByOption;
import com.navbara_pigeons.wasteless.enums.ProductSortByOption;
import com.navbara_pigeons.wasteless.enums.SortByOption;
import com.navbara_pigeons.wasteless.enums.UserSortByOption;
import com.navbara_pigeons.wasteless.exception.InvalidPaginationInputException;
import lombok.Getter;

/**
 * A class that conveniently holds all of the needed information in order to paginate and sort a
 * list of items. This class follows the builder pattern and allows the client to customise the
 * values as required while still having valid default parameters.
 *
 * <p>Lombok has been configured to allow only Getters so client is forced to use the Builder
 * Pattern.
 */
@Getter
public class PaginationBuilder {

  private final Class<?> entity;
  private Integer pagStartIndex = 0;
  private Integer pagEndIndex;
  private SortByOption sortField;
  private boolean sortAscending = true;

  /**
   * Pagination Builder Constructor, sets the entity and parses the default sort field
   *
   * @param entity              The class of entity that the Pagination/Sorting is for
   * @param defaultSortByOption A enum value of the Java field name from the Entity class to sort
   *                            by
   */
  public PaginationBuilder(Class<?> entity, SortByOption defaultSortByOption)
      throws InvalidPaginationInputException {
    this.entity = entity;
    if (defaultSortByOption == null) {
      sortField = getDefaultSortByOption();
    } else {
      sortField = defaultSortByOption;
    }
  }

  /**
   * Parse a Sort By String from the Client.
   *
   * @param sortByOption Defines the field to sort by
   * @return The Pagination Builder Object, used for chaining methods
   */
  public PaginationBuilder withSortBy(SortByOption sortByOption) {
    sortField = sortByOption;
    return this;
  }

  /**
   * Set the Pagination Start Index. Also check that it is a valid value (startIndex <= endIndex)
   *
   * @param pagStartIndex The Integer of the index of the first item to be returned
   * @return The Pagination Builder Object, used for chaining methods
   * @throws InvalidPaginationInputException Invalid Pagination value, breaks rule startIndex <=
   *                                         endIndex
   */
  public PaginationBuilder withPagStartIndex(Integer pagStartIndex)
      throws InvalidPaginationInputException {
    if (pagStartIndex != null) {
      if (pagStartIndex < 0) {
        throw new InvalidPaginationInputException(
            "The pagination start index must be greater than zero");
      }
      if (this.pagEndIndex != null && pagStartIndex > this.pagEndIndex) {
        throw new InvalidPaginationInputException(
            "The pagination start index must be smaller or equal to the pagination end index");
      }
      this.pagStartIndex = pagStartIndex;
    }
    return this;
  }

  /**
   * Set the Pagination End Index. Also check that it is a valid value (startIndex <= endIndex)
   *
   * @param pagEndIndex The Integer of the index of the last item to be returned
   * @return The Pagination Builder Object, used for chaining methods
   * @throws InvalidPaginationInputException Invalid Pagination value, breaks rule startIndex <=
   *                                         endIndex
   */
  public PaginationBuilder withPagEndIndex(Integer pagEndIndex)
      throws InvalidPaginationInputException {
    if (pagEndIndex != null) {
      if (this.pagStartIndex > pagEndIndex) {
        throw new InvalidPaginationInputException(
            "The pagination start index must be smaller or equal to the pagination end index");
      }
      this.pagEndIndex = pagEndIndex;
    }
    return this;
  }

  /**
   * Set the Sorting Direction, in other words. Is the sorting in Ascending order?
   *
   * @param sortAscending Whether the list should be in Ascending order (Boolean value)
   * @return The Pagination Builder Object, used for chaining methods
   */
  public PaginationBuilder withSortAscending(boolean sortAscending) {
    this.sortAscending = sortAscending;
    return this;
  }

  public String getSortByFieldName() throws InvalidPaginationInputException {
    String fieldName = null;
    // Switch statements apparently don't work for Class<?>
    if (User.class.equals(entity)) {
      switch ((UserSortByOption) this.getSortField()) {
        case FIRST_NAME:
          fieldName = "firstName";
          break;
        case MIDDLE_NAME:
          fieldName = "middleName";
          break;
        case LAST_NAME:
          fieldName = "lastName";
          break;
        case NICKNAME:
          fieldName = "nickname";
          break;
      }
    } else if (Product.class.equals(entity)) {
      switch ((ProductSortByOption) this.getSortField()) {
        case NAME:
          fieldName = "name";
          break;
        case MANUFACTURER:
          fieldName = "manufacturer";
          break;
        case RRP:
          fieldName = "recommendedRetailPrice";
          break;
        case CREATED_DATE:
          fieldName = "created";
          break;
      }
    } else if (MarketListing.class.equals(entity)) {
      switch ((MarketListingSortByOption) this.getSortField()) {
        case TITLE:
          fieldName = "title";
          break;
        case CREATED_DATE:
          fieldName = "created";
          break;
        case DISPLAY_PERIOD_END_DATE:
          fieldName = "displayPeriodEnd";
          break;
      }
    } else if (Listing.class.equals(entity)) {
      switch ((ListingSortByOption) this.getSortField()) {
        case QUANTITY:
          fieldName = "quantity";
          break;
        case PRICE:
          fieldName = "price";
          break;
        case CREATED_DATE:
          fieldName = "created";
          break;
        case CLOSES_DATE:
          fieldName = "closes";
          break;
      }
    } else if (InventoryItem.class.equals(entity)) {
      switch ((InventorySortByOption) this.getSortField()) {
        case QUANTITY:
          fieldName = "quantity";
          break;
        case PPI:
          fieldName = "pricePerItem";
          break;
        case TOTAL_PRICE:
          fieldName = "totalPrice";
          break;
        case MANUFACTURED_DATE:
          fieldName = "manufactured";
          break;
        case SELL_BY_DATE:
          fieldName = "sellBy";
          break;
        case EXPIRY_DATE:
          fieldName = "bestBefore";
          break;
      }
    } else {
      throw new InvalidPaginationInputException("Unknown entity used in Pagination Builder");
    }
    return fieldName;
  }

  private SortByOption getDefaultSortByOption() throws InvalidPaginationInputException {
    SortByOption defaultSortByOption;
    // Switch statements apparently don't work for Class<?>
    if (User.class.equals(entity)) {
      defaultSortByOption = FIRST_NAME;
    } else if (Product.class.equals(entity)) {
      defaultSortByOption = ProductSortByOption.NAME;
    } else if (MarketListing.class.equals(entity)) {
      defaultSortByOption = MarketListingSortByOption.CREATED_DATE;
    } else if (Listing.class.equals(entity)) {
      defaultSortByOption = ListingSortByOption.CREATED_DATE;
    } else if (InventoryItem.class.equals(entity)) {
      defaultSortByOption = InventorySortByOption.QUANTITY;
    } else {
      throw new InvalidPaginationInputException("Unknown entity used in Pagination Builder");
    }
    return defaultSortByOption;
  }
}
