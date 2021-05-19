Feature: Create Inventory
#So that I can manage my stocks of products, as a business administrator, I need to be able to manage the inventory of my business.
  Background:
    Given A user is logged in
    And has a business "Daft Donkeys" with type "Accommodation and Food Services"
    And  with a product "Donkey Kong"

#CRud: Inventory
#Note: Entries in the inventory are (batches of) products that are in stock.  N.B.: this is not the same thing as the catalogue of products of interest to us.  The development team can choose, ideally in consultation with the SM/PO,  the best way of displaying the inventory.
#AC1: As a logged in business administrator I can access my inventory.  Other users can not see it.
  Scenario: the user can see their inventory
    Given the user has created an inventory item with the product
    When I retrieve my inventory
    Then The inventory item is listed
  # To those who wish to test acting as someone else:
  # a) is already tested in the inventory controller tests and
  # b) we spent 2 hours trying to figure out how to login after changing login: calling the user controller's login method does not work; setting SecurityContextHolder.getContext.setAuthentication does not; in the user controller getAuthentication still returns the user that was logged in first.

#AC2: I can add entries to the inventory of my business.  Each represents one or more physical product items.  For now, entry is done via an appropriate form—later we may support bulk loading.  Each inventory entry has a mandatory product code, corresponding to the appropriate catalogue item.
  Scenario: I can add entries to my inventory
    When I add an inventory entry, with the a product with quantity 1 and expiry date in the future
    Then When I retrieve my inventory the entry is listed
#AC3: Inventory entries have additional fields: quantity*, price per item, total price, and at least one date related to the product* (see AC4).
#AC4: The date related to the entry could be one or more of Prepared/manufactured on, Sell by, Best before, Expiry date (default). The expiry date is mandatory. Dates are easy to enter (e.g. date picker), are validated (dates in the past/future), and displayed so that there is no confusion.  Note:  Subsequent stories will involve displaying information about specific sale listings—only the dates that are filled in will be shown to the public.
#AC5: The currency (both the symbol and the currency code) is shown automatically for any prices in the inventory, e.g. “$30 NZD”. The creator’s (acting as individual or business) location determines the currency. Use an API such as: https://restcountries.eu/ to be current.
#AC6: The thumbnail of the primary image for the corresponding catalogue entry is displayed alongside the inventory entry.
#AC7: Inventory entries are displayed on an appropriate page. There is a default ordering. A combo box allows the viewer to reorder the products (e.g. by product code, by creation date, by quantity, by price,  …).
