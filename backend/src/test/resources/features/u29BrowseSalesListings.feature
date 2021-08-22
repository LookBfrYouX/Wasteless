Feature: U29 Browse Sales Listings

  Scenario: Endpoint exists
    When I send an empty request to the endpoint "/listings/search"
    Then I do not receive a 404 error.

  Scenario: AC2 - Check all listings are returned when no filtering is applied
    When I send a valid request with no filters to "/listings/search"
    Then I receive all the current listings

  Scenario: AC4 - Check that filtering and sorting works
    When I send a valid request sorted by price to "/listings/search"
    Then The listings are ordered by price

  Scenario: AC5 - Check the filtering by business types works
    When I send a valid request filtered by business type "RETAIL" to "/listings/search"
    Then Only listings for businesses of type "Retail Trade" are returned

  Scenario: AC6 - Listings can be limited by searching by product name
    When I send a valid request searching for listings with products containing "Milk" to "/listings/search"
    Then Listings with products containing the word "Milk" are shown

  Scenario: AC7 - Listings can be limited by setting a price range
    When I send a valid request with a price range of 10.0 to 20.0 to "/listings/search"
    Then I am only shown listings more than 10.0 and less than 20.0

  Scenario: AC8 - Listings can be limited by searching by business name
    When I send a valid request searching for listings with products offered by "TestName" to "/listings/search"
    Then Listings from sellers called "TestName" are shown

  Scenario: AC9 - Listings can be limited by searching by business address
    When I send a valid request searching for listings with business located in "Christchurch" to "/listings/search"
    Then Listings from sellers located in "Christchurch" are shown

  Scenario: AC10 - Listings can be limited by searching for a date range
    When I send a valid request searching for listings closing between now and in three months to "/listings/search"
    Then Listings closing in the next three months are shown

