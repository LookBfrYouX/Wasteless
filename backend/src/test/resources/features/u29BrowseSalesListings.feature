Feature: U29 Browse Sales Listings

#So that I can find sales I might be interested in, as a logged-in individual user, I need a way to browse currently-available sales listings.
# Check for no 404 on going to listings page (i.e. endpoint exists).
  Scenario: Endpoint exists
    When I send a request to the endpoint "/listings/search"
    Then I do not receive a 404 error.

#AC1: FRONTEND!


#AC2: If no filtering is applied then all current sales listings are displayed.  There could be a large number of these.  For each listing, the relevant data from U22 is included, together with information about the seller (a business) — such as name and some location detail.
# Check total count is total with no filters applied.
# Check product name, expiry, sales listing end, other U22 data is returned.

#AC3: FRONTEND!

#AC4: The sales listings may be ordered in various ways (product name, country/city of seller, expiry date, seller, price, …).
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
