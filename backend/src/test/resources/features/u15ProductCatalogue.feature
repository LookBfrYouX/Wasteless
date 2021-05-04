
Feature: U15 Product Catalogue

  Scenario: AC1 - Accessing the catalogue.
    Given "Bobby" is signed in and administers a business "Bobbys Barnacles" with a product "barnacles"
    When "Bobby" requests his product catalogue
    Then The product "barnacles" is displayed
    When "Tim" tries to access another business called "Bobbys Barnacles" product "barnacles"
    Then the product "barnacles" is not displayed


  Scenario: AC2 - Adding items to catalogue.
    Given a user with name "Amish" has a "Retail Trade" business "Ancient Antiques" in "Australia"
    When "Amish" creates a product "Arty Artichokes" to sell at business "Ancient Antiques"
    When "Amish" requests his product catalogue
    Then The product "Ancient Antiques" is displayed

#  AC2: I can add items to my catalogue.  For now, this is done via an appropriate form—later
#  we may support bulk loading.  Each catalogue entry has a mandatory product code.
#  Product codes are unique and provide a conveniently short way to identify products.
#  Teams may decide whether to allow users to choose product codes (e.g. for mnemonic reasons)
#  or whether codes should be automatically generated.

#  AC3: Catalogue items have additional fields: full name (mandatory), description, manufacturer,
#  recommended retail price (RRP).  The date a product is added to the catalogue is automatically recorded.
  Scenario: AC3 - Catalog item fields
    Given a user with name "Carl" has a "Retail Trade" business "Crazy Cheeses" in "Canada"
    When "Carl" creates a product "Colby Cheese" made by "Cheese Company" with RRP "19.99"
    Then the ID, date created is set automatically and the currency is set to "CAD"
#  AC4: The currency (both the symbol and the currency code) is shown automatically
#  for RRP (and any other prices in the app), e.g. “$30 NZD”. The creator’s location determines the currency.
#  Use an API such as: https://restcountries.eu/ to be current.

#  AC5: Catalogue entries are displayed in an appropriate (probably tabular) form. There is a default ordering.
#  A combo box allows the viewer to reorder the products (e.g. by product code, by name,
#  by creation date, by price,  …).
