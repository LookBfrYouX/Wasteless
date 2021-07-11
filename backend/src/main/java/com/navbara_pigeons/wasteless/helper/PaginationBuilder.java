package com.navbara_pigeons.wasteless.helper;

import lombok.Getter;

import java.lang.reflect.Field;

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

  Object entity;
  Integer pagStartIndex = 0;
  Integer pagEndIndex;
  String sortField;
  boolean sortAscending = true;

  /**
   * Pagination Builder Constructor, sets the entity and parses the default sort field
   *
   * @param entity The object that the Pagination/Sorting is for
   * @param defaultSortField A String value of the Java field name from the Entity class
   */
  public PaginationBuilder(Object entity, String defaultSortField) {
    this.entity = entity;
    // Use the parse method as it checks if the sort field exists in the entity
    if (defaultSortField != null) {
      parseSortByString(defaultSortField + "-acs");
    }
  }

  /**
   * Parse a Sort By String from the Client.
   *
   * @param sortByString Defines the field to sort by and the direction (ascending or descending).
   *     In the format "fieldName-<acs/desc>"
   * @return The Pagination Builder Object, used for chaining methods
   */
  public PaginationBuilder withSortByString(String sortByString) {
    if (sortByString != null) {
      parseSortByString(sortByString);
    }
    return this;
  }

  /**
   * Set the Pagination Start Index. Also check that it is a valid value (startIndex <= endIndex)
   *
   * @param pagStartIndex The Integer of the index of the first item to be returned
   * @return The Pagination Builder Object, used for chaining methods
   * @throws IllegalArgumentException Invalid Pagination value, breaks rule startIndex <= endIndex
   */
  public PaginationBuilder withPagStartIndex(Integer pagStartIndex)
      throws IllegalArgumentException {
    if (pagStartIndex != null) {
      if (pagStartIndex < 0) {
        throw new IllegalArgumentException("The pagination start index must be greater than zero");
      }
      if (this.pagEndIndex != null && pagStartIndex > this.pagEndIndex) {
        throw new IllegalArgumentException(
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
   * @throws IllegalArgumentException Invalid Pagination value, breaks rule startIndex <= endIndex
   */
  public PaginationBuilder withPagEndIndex(Integer pagEndIndex) throws IllegalArgumentException {
    if (pagEndIndex != null) {
      if (this.pagStartIndex > pagEndIndex) {
        throw new IllegalArgumentException(
            "The pagination start index must be smaller or equal to the pagination end index");
      }
      this.pagEndIndex = pagEndIndex;
    }
    return this;
  }

  /**
   * Set the field to sort by. Also check that the newField exists in the selected Entity.
   *
   * @param sortField The new sort Field value
   * @return The Pagination Builder Object, used for chaining methods
   */
  public PaginationBuilder withSortField(String sortField) {
    if (isValidFieldName(sortField)) {
      this.sortField = sortField;
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

  /**
   * Private method to check if a passed field name (String) is in the currently selected entity.
   *
   * @param fieldName The field name in question
   * @return True if the field name in in the entity, False if otherwise
   * @throws IllegalArgumentException If the value is invalid
   */
  private boolean isValidFieldName(String fieldName) throws IllegalArgumentException {
    boolean validField = false;

    for (Field field : ((Class) entity).getDeclaredFields()) {
      if (field.getName().equals(fieldName)) {
        validField = true;
        break;
      }
    }

    if (!validField) {
      throw new IllegalArgumentException(
          "The passed in field to sort by does not exist in the "
              + ((Class) this.entity).getName()
              + " class");
    }

    return true;
  }

  /**
   * Parse a SortBy string sent from the client. Check that it is in the correct format and then set
   * the sort field and its direction (sort ascending)
   *
   * @param sortByString The SortBy string to parse
   * @throws IllegalArgumentException SortBy string has an invalid format
   */
  private void parseSortByString(String sortByString) throws IllegalArgumentException {
    String[] splitSortBy = sortByString.split("-");

    if (splitSortBy.length != 2) {
      throw new IllegalArgumentException(
          "SortByString must be in 'entityFieldName-<acs/desc>' format");
    }

    String passedSortField = splitSortBy[0];
    String passedSortAscending = splitSortBy[1];

    if (isValidFieldName(passedSortField)) {
      this.sortField = passedSortField;
    }

    if (passedSortAscending.equals("desc")) {
      this.sortAscending = false;
    }
  }
}
