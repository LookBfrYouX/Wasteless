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

#AC5: A suitable UI control allows me to limit the results to one or more business types.  You may add more business types if you wish (e.g. from the source given in AC3 of U5 ) in order to make this filter more useful.
  Scenario: AC5 - Check the filtering by business types works
    When I send a valid request filtered by business type "RETAIL" to "/listings/search"
    Then Only listings for businesses of type "Retail Trade" are returned

#AC6: I can limit the results by typing, in a suitable field, all or part of a product name.
  Scenario: AC6 - Listings can be limited by searching by product name
    When I send a valid request searching for listings with products containing "Milk" to "/listings/search"
    Then Listings with products containing the word "Milk" are shown

#AC7: I can limit the results by setting a price range.
  Scenario: AC7 - Listings can be limited by setting a price range
    When I send a valid request with a price range of 10.0 to 20.0 to "/listings/search"
    Then I am only shown listings more than 10.0 and less than 20.0


#AC8: I can limit the results by typing, in a suitable field, all or part of the name of the seller (business).
  Scenario: AC8 - Listings can be limited by searching by business name
    When I send a valid request searching for listings with products offered by "TestName" to "/listings/search"
    Then Listings from sellers called "TestName" are shown

#AC9: I can limit the results by typing, in a suitable field, all or part of the address of the seller (business).  It would be sufficient to enter one of the region/suburb, city or country but you are free to allow more than one of these to be specified.  Note: in future stories we may be more interested in the seller’s distance from us than in their address.
# Check search by address works.
  Scenario: AC9 - Listings can be limited by searching by business address
    When I send a valid request searching for listings with business located in "Christchurch" to "/listings/search"
    Then Listings from sellers located in "Christchurch" are shown

#AC10: I can limit the results by setting a closing date range.
  Scenario: AC10 - Listings can be limited by searching for a date range
    When I send a valid request searching for listings created between three months ago and now to "/listings/search"
    Then Listings created in the last month are shown
# Check filter by single date works.

#AC11: FRONTEND!
