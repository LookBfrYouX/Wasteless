Feature: U22

#  AC2: I can add a listing using an appropriate sequence of UI actions. This includes selecting
#  an inventory entry: the application will then transfer the appropriate fields to the sale listing.
#  These include the number of items for sale, which must be greater than zero and less than or
#  equal to the number in the corresponding inventory entry. The name and thumbnail image
#  of the corresponding product should also be included. Note: If a sale listing does not include
#  all of the items in an inventory entry then the remaining items may be included in other
#  sale listings (perhaps with dif erent closing dates and prices). However, we cannot list for
#  sale more items than are in the corresponding inventory entry. For example, if I have 50
#  punnets of strawberries due to expire on Saturday, I could of er 30 of them in one sale
#  listing and the remaining 20 in another — but I couldn’t have two listings of 30.
#
#  AC3: If the number of items in the listing is less than that in the corresponding inventory
#  entry, and the inventory contains a non-null item price, then that is used to calculate the
#  total price for the listing.
#
#  AC4: The price for the listing may be overridden. An optional field for further information
#  (e.g. “seller may be willing to consider near offers”) is available.

  Background:
    Given a user has a business "Jovial Jerky" in "New Zealand"
    # NB: test countryData.json only has NZ
    And the business has the product "Jenkin's Jerky" with RRP of 7.99
#    And they have 40 in their inventory with a price of 4.50 expiring on "2022-04-13"

#  Scenario: AC3 - less than amount in inventory

#
#  Scenario: AC2 footnote, two listings below amount in inventory
#    Given a listing with quantity 20 and price 150.00
#    Then another listing with quantity 20 and price 150.00 succeeds
#
#  Scenario: AC2 footnote, two listings above amount in inventory
#    Given a listing with quantity 20 and price 150.00
#    Then another listing with quantity 21 and price 152.00 fails

