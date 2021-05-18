Feature: Create Inventory
#So that I can manage my stocks of products, as a business administrator, I need to be able to manage the inventory of my business.
  Background: As a business administrator for "Daft Donkeys", I need to be able to manage the inventory of my business.
#CRud: Inventory
#Note: Entries in the inventory are (batches of) products that are in stock.  N.B.: this is not the same thing as the catalogue of products of interest to us.  The development team can choose, ideally in consultation with the SM/PO,  the best way of displaying the inventory.
#AC1: As a logged in business administrator I can access my inventory.  Other users can not see it.
  Scenario: I can see my inventory and others can not.
  Given My business has an item called "Dreary Dwergezel"
  When I retrieve my inventory
  Then The "Dreary Dwergezel" is listed
  When Someone else retrieves my inventory
  Then An error is shown
#AC2: I can add entries to the inventory of my business.  Each represents one or more physical product items.  For now, entry is done via an appropriate form—later we may support bulk loading.  Each inventory entry has a mandatory product code, corresponding to the appropriate catalogue item.
#AC3: Inventory entries have additional fields: quantity*, price per item, total price, and at least one date related to the product* (see AC4).
#AC4: The date related to the entry could be one or more of Prepared/manufactured on, Sell by, Best before, Expiry date (default). The expiry date is mandatory. Dates are easy to enter (e.g. date picker), are validated (dates in the past/future), and displayed so that there is no confusion.  Note:  Subsequent stories will involve displaying information about specific sale listings—only the dates that are filled in will be shown to the public.
#AC5: The currency (both the symbol and the currency code) is shown automatically for any prices in the inventory, e.g. “$30 NZD”. The creator’s (acting as individual or business) location determines the currency. Use an API such as: https://restcountries.eu/ to be current.
#AC6: The thumbnail of the primary image for the corresponding catalogue entry is displayed alongside the inventory entry.
#AC7: Inventory entries are displayed on an appropriate page. There is a default ordering. A combo box allows the viewer to reorder the products (e.g. by product code, by creation date, by quantity, by price,  …).
