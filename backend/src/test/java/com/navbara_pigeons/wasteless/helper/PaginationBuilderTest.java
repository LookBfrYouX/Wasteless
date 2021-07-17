package com.navbara_pigeons.wasteless.helper;

import com.navbara_pigeons.wasteless.entity.Product;
import com.navbara_pigeons.wasteless.entity.User;
import com.navbara_pigeons.wasteless.exception.InvalidPaginationInputException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PaginationBuilderTest {

  @Test
  public void constructorWithValidParams() {
    // Arrange
    Class<User> entity = User.class;
    String defaultSortField = "firstName";

    // Act
    Assertions.assertDoesNotThrow(() -> {
      PaginationBuilder paginationBuilder = new PaginationBuilder(entity, defaultSortField);

      // Assert
      Assertions.assertEquals(entity, paginationBuilder.getEntity());
      Assertions.assertEquals(defaultSortField, paginationBuilder.getSortField());
    });
  }

  @Test
  public void constructorWithInvalidSortField() {
    // Arrange
    Class<User> entity = User.class;
    String defaultSortField = "nonExistingField";

    // Act & Assert
    Assertions.assertThrows(InvalidPaginationInputException.class, () -> {
      new PaginationBuilder(entity, defaultSortField);
    });
  }

  @Test
  public void validPaginationAndValidSortingParameters() {
    // Arrange
    Class<Product> entity = Product.class;
    String defaultSortField = "manufacturer";
    Integer pagStartIndex = 0;
    Integer pagEndIndex = 0;

    // Act
    PaginationBuilder paginationBuilder = null;
    try {
      paginationBuilder = new PaginationBuilder(entity, defaultSortField)
          .withPagStartIndex(pagStartIndex)
          .withPagEndIndex(pagEndIndex);
    } catch (InvalidPaginationInputException e) {
      e.printStackTrace();
    }

    // Assert
    Assertions.assertEquals(pagStartIndex, paginationBuilder.getPagStartIndex());
    Assertions.assertEquals(pagEndIndex, paginationBuilder.getPagEndIndex());
    Assertions.assertEquals(defaultSortField, paginationBuilder.getSortField());
    Assertions.assertEquals(entity, paginationBuilder.getEntity());
  }

  @Test
  public void testDefaultParameters() {
    // Arrange
    Class<Product> entity = Product.class;
    String defaultSortField = "name";
    boolean defaultSortAscending = true;
    Integer defaultPagStartIndex = 0;
    Integer defaultPagEndIndex = null;

    // Act
    PaginationBuilder paginationBuilder = null;
    try {
      paginationBuilder = new PaginationBuilder(entity, defaultSortField);
    } catch (InvalidPaginationInputException e) {
      e.printStackTrace();
    }

    // Assert
    Assertions.assertEquals(defaultSortAscending, paginationBuilder.isSortAscending());
    Assertions.assertEquals(defaultPagStartIndex, paginationBuilder.getPagStartIndex());
    Assertions.assertEquals(defaultPagEndIndex, paginationBuilder.getPagEndIndex());
  }

  @Test
  public void testInvalidPaginationIndexes() {
    // Arrange
    Class<Product> entity = Product.class;
    String defaultSortField = "name";
    Integer pagStartIndex = 2;
    Integer pagEndIndex = pagStartIndex - 1;

    // Act
    PaginationBuilder paginationBuilder = null;
    try {
      paginationBuilder = new PaginationBuilder(entity, defaultSortField)
          .withPagStartIndex(pagStartIndex);
    } catch (InvalidPaginationInputException e) {
      e.printStackTrace();
    }

    // Assert
    PaginationBuilder finalPaginationBuilder = paginationBuilder;
    Assertions.assertThrows(InvalidPaginationInputException.class, () -> {
      finalPaginationBuilder.withPagEndIndex(pagEndIndex);
    });
  }

  @Test
  public void changingToValidSortField() {
    // Arrange
    Class<User> entity = User.class;
    String defaultSortField = "firstName";
    String newValidSortField = "lastName";
    PaginationBuilder paginationBuilder = null;
    try {
      paginationBuilder = new PaginationBuilder(entity, defaultSortField);
    } catch (InvalidPaginationInputException e) {
      e.printStackTrace();
    }

    // Act & Assert
    Assertions.assertEquals(defaultSortField, paginationBuilder.getSortField());
    try {
      paginationBuilder.withSortField(newValidSortField);
    } catch (InvalidPaginationInputException e) {
      e.printStackTrace();
    }
    Assertions.assertEquals(newValidSortField, paginationBuilder.getSortField());
  }

  @Test
  public void changingToInvalidSortField() {
    // Arrange
    Class<User> entity = User.class;
    String defaultSortField = "firstName";
    String newInvalidSortField = "nonExistingField";
    PaginationBuilder paginationBuilder = null;
    try {
      paginationBuilder = new PaginationBuilder(entity, defaultSortField);
    } catch (InvalidPaginationInputException e) {
      e.printStackTrace();
    }

    // Act & Assert
    Assertions.assertEquals(defaultSortField, paginationBuilder.getSortField());
    PaginationBuilder finalPaginationBuilder = paginationBuilder;
    Assertions.assertThrows(InvalidPaginationInputException.class, () -> {
      finalPaginationBuilder.withSortField(newInvalidSortField);
    });
  }

  @Test
  public void changingToValidSortAscending() {
    // Arrange
    Class<User> entity = User.class;
    String defaultSortField = "firstName";
    Boolean newValidSortAscending = false;

    // Act
    PaginationBuilder paginationBuilder = null;
    try {
      paginationBuilder = new PaginationBuilder(entity, defaultSortField)
          .withSortAscending(newValidSortAscending);
    } catch (InvalidPaginationInputException e) {
      e.printStackTrace();
    }

    // Assert
    Assertions.assertEquals(newValidSortAscending, paginationBuilder.isSortAscending());
  }
}
