Feature: U19 Create Inventory

  Background:
    Given these users exist
      | emailAddress     | password          | firstName |
      | Bobby@email.test | TestUserPassword1 | Bob       |
      | Tim@email.test   | TestUserPassword2 | Tim       |

  Scenario: AC1 - Accessing the inventory
    Given "Bobby@email.test" administers a business "Bobbys Barnacles" with an inventory item "barnacles"
    When "Bobby@email.test" is logged in with password "TestUserPassword1"
    And He tries to access his business called "Bobbys Barnacles" inventory
    Then The inventory item "barnacles" is displayed

    When "Tim@email.test" is logged in with password "TestUserPassword2"
    And He tries to access another business called "Bobbys Barnacles" inventory
    Then An error is thrown with message "Insufficient Privileges"


#AC2: I can add entries to the inventory of my business.  Each represents one or more physical product items.
# For now, entry is done via an appropriate form—later we may support bulk loading.
# Each inventory entry has a mandatory product code, corresponding to the appropriate catalogue item.

#AC3: Inventory entries have additional fields: quantity*, price per item, total price, and at least
# one date related to the product* (see AC4).

#AC4: The date related to the entry could be one or more of Prepared/manufactured on, Sell by,
# Best before, Expiry date (default). The expiry date is mandatory. Dates are easy to enter
# (e.g. date picker), are validated (dates in the past/future), and displayed so that there is no confusion.
# Note: Subsequent stories will involve displaying information about specific sale listings—only the
# dates that are filled in will be shown to the public.

#AC5: The currency (both the symbol and the currency code) is shown automatically for any prices in
# the inventory, e.g. “$30 NZD”. The creator’s (acting as individual or business) location determines
# the currency. Use an API such as: https://restcountries.eu/ to be current.

#AC6: The thumbnail of the primary image for the corresponding catalogue entry is displayed alongside
# the inventory entry.

#AC7: Inventory entries are displayed on an appropriate page. There is a default ordering. A combo box
# allows the viewer to reorder the products (e.g. by product code, by creation date, by quantity, by price,  …).
