Feature: U29 Browse Sales Listings

  Scenario: Endpoint exists
    When I send an empty request to the endpoint "/listings/search"
    Then I do not receive a 404 error.

  Scenario: AC2 - Check all listings are returned when no filtering is applied
    When I send a valid request with no filters to "/listings/search"
    Then I receive all the current listings


#AC4: The sales listings may be ordered in various ways (product name, country/city of seller, expiry date, seller, price, …).
  Scenario: AC4 - Check that filtering and sorting works
    When I send a valid request sorted by price to "/listings/search"
    Then The listings are ordered by price
# Check that filtering/sorting works.

#AC5: A suitable UI control allows me to limit the results to one or more business types.  You may add more business types if you wish (e.g. from the source given in AC3 of U5 ) in order to make this filter more useful.
# Check filtering by business type works.

#AC6: I can limit the results by typing, in a suitable field, all or part of a product name.
# Check search product name works.

#AC7: I can limit the results by setting a price range.
# Check price range filtering works.

#AC8: I can limit the results by typing, in a suitable field, all or part of the name of the seller (business).
# Check search by seller works.

#AC9: I can limit the results by typing, in a suitable field, all or part of the address of the seller (business).  It would be sufficient to enter one of the region/suburb, city or country but you are free to allow more than one of these to be specified.  Note: in future stories we may be more interested in the seller’s distance from us than in their address.
# Check search by address works.

#AC10: I can limit the results by setting a closing date range.
# Check filter by single date works.

#AC11: FRONTEND!
