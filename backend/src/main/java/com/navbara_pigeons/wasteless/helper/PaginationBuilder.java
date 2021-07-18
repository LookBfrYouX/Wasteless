package com.navbara_pigeons.wasteless.helper;

import com.navbara_pigeons.wasteless.enums.SortByOption;
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
   * @param entity           The class of entity that the Pagination/Sorting is for
   * @param defaultSortField A String value of the Java field name from the Entity class
   */
  public PaginationBuilder(Class<?> entity, SortByOption defaultSortByOption) {
    this.entity = entity;
    sortField = defaultSortByOption;
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

  public String getSortByFieldName() {
    return "TODO";
  }

//  /**
//   * Private method to check if a passed field name (String) is in the currently selected entity.
//   *
//   * @param fieldName The field name in question
//   * @return True if the field name in in the entity, False if otherwise
//   * @throws InvalidPaginationInputException If the value is invalid
//   */
//  private boolean isValidFieldName(String fieldName) throws InvalidPaginationInputException {
//    boolean validField = false;
//
//    for (Field field : entity.getDeclaredFields()) {
//      if (field.getName().equals(fieldName)) {
//        validField = true;
//        break;
//      }
//    }
//
//    if (!validField) {
//      throw new InvalidPaginationInputException(
//          "The passed in field to sort by does not exist in the "
//              + entity.getName()
//              + " class");
//    }
//
//    return true;
//  }
//
//  /**
//   * Parse a SortBy string sent from the client. Check that it is in the correct format and then set
//   * the sort field and its direction (sort ascending). Note: Any sort string that does not match
//   * "desc" will by default be Ascending
//   *
//   * @param sortByString The SortBy string to parse
//   * @throws InvalidPaginationInputException SortBy string has an invalid format
//   */
//  private void parseSortByString(String sortByString) throws InvalidPaginationInputException {
//    String[] splitSortBy = sortByString.split("-");
//
//    if (splitSortBy.length != 2) {
//      throw new InvalidPaginationInputException(
//          "SortByString must be in 'entityFieldName-<acs/desc>' format");
//    }
//
//    String passedSortField = splitSortBy[0];
//    String passedSortAscending = splitSortBy[1];
//
//    if (isValidFieldName(passedSortField)) {
//      this.sortField = passedSortField;
//    }
//
//    if (passedSortAscending.equals("desc")) {
//      this.sortAscending = false;
//    }
//  }
}
