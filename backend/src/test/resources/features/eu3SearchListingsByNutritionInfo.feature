# Created by Niko at 13/09/2021
Feature: EU3 Filter Sales Listings With Nutritional Information

  Scenario: AC1 - Check that filtering by diets works
    When I send a valid request to "/listings/search" with vegan set to "true"
    Then only the products where "isVegan" is "true" are returned

  Scenario: AC2 - Check that filtering with min and max Nutriscore works
    When I send a valid request to "/listings/search" with max nutriscore being "A" and min nutriscore being "C"
    Then only the products with nutriscore values being between "A" and "C" inclusive are shown

  Scenario: AC2 - Check that empty Nutriscore defaults to all products being shown
    When I send a valid request to "/listings/search" with no max or min nutriscore values set
    Then all the products are shown

  Scenario: AC3 - Check that filtering with min and max NOVA group works
    When I send a valid request to "/listings/search" with max Nova group being "1" and min nutriscore being "3"
    Then only the products with nova values being between "1" and "3" inclusive are shown

  Scenario: AC3 - Check that empty NOVA group defaults to all products being shown
    When I send a valid request to "/listings/search" with no max or min nova group values set
    Then all the products are shown

  Scenario: AC4 - Check that filtering with Nutritional quantity information works
    When I send a valid request to "/listings/search" with with "fat" set to "HIGH", "saturatedFat" set to "HIGH","sugars" set to "MODERATE" and "salt" set to "LOW"
    Then only the products with "fat" set to "HIGH", "saturatedFat" set to "HIGH","sugars" set to "MODERATE" and "salt" set to "LOW" are shown


