# Created by Niko at 13/09/2021
Feature: EU3 Filter Sales Listings With Nutritional Information
  Scenario AC1 - Check that filtering by singular dietary option works:
    When I send a valid request to "/listings/search" with vegan set to "true"
    Then only the products that are vegan are shown

  Scenario AC1 - Check that filtering by multiple dietary option works:
    When I send a valid request to "/listings/search" with vegan set to "true" and gluten free set to "true"
    Then only the products that are vegan and gluten free are shown

  Scenario: AC3 -  Filter max NOVA group in listings search
    Given a user is logged in
    When I send a request to "/listings/search" with max NOVA group set to 2
    Then only listings with a NOVA group of 1 and 2 are received

  Scenario: AC3 -  Filter min NOVA group in listings search
    Given a user is logged in
    When I send a request to "/listings/search" with min NOVA group set to 3
    Then only listings with a NOVA group of 3 and 4 are received

  Scenario: AC3 -  Filter range NOVA group in listings search
    Given a user is logged in
    When I send a request to "/listings/search" with min NOVA group set to 2 and max nova group set to 3
    Then only listings with a NOVA group of 2 and 3 are received

  Scenario: AC3 - Filter by max nutri-score in listings search
    Given a user is logged in
    When I send a request to "/listings/search" with max nutri-score set to "B"
    Then only listings with a nutri-score of "A" and "B" are received

  Scenario: AC3 - Filter by min nutri-score in listings search
    Given a user is logged in
    When I send a request to "/listings/search" with min nutri-score group set to "D"
    Then only listings with a nutri-score of "D" and "E" are received

  Scenario: AC3 - Filter by nutri-score range in listings search
    Given a user is logged in
    When I send a request to "/listings/search" with min nutri-score set to "B" and max nutri-score set to "D"
    Then only listings with a NOVA group of "B", "C" and "D" are received




