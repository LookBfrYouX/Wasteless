# Created by Niko at 13/09/2021
Feature: EU3 Filter Sales Listings With Nutritional Information
  Scenario AC1 - Check that filtering by singular dietary option works:
    When I send a valid request to "/listings/search" with vegan set to "true"
    Then only the products that are vegan are shown

  Scenario AC1 - Check that filtering by multiple dietary option works:
    When I send a valid request to "/listings/search" with vegan set to "true" and gluten free set to "true"
    Then only the products that are vegan and gluten free are shown

  Scenario: AC4 - Check that filtering by fat works
    When I send a valid request to "/listings/search" with "fat" set to "HIGH"
    Then only the products with "fat" set to "HIGH" are shown

  Scenario: AC4 - Check that filtering by saturated fat works
    When I send a valid request to "/listings/search" with  "saturatedFat" set to "HIGH" and "MODERATE"
    Then only the products with "saturatedFat" set to "HIGH" and "MODERATE" are shown

  Scenario: AC4 - Check that filtering by salt works
    When I send a valid request to "/listings/search" with "salt" set to "LOW"
    Then only the products with "salt" set to "LOW" are shown

  Scenario: AC4 - Check that filtering by sugars works
    When I send a valid request to "/listings/search" with  "sugars" set to "MODERATE"
    Then only the products with "sugars" set to "MODERATE" are shown




