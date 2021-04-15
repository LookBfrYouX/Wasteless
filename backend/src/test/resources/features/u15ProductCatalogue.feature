Feature: U15 Product Catalogue

  Scenario: AC1 - Accessing the catalogue.
    Given a user with name "Bobby" is logged in and administers a business called "Bobbys Barnacles"
    When "Bobby" accesses "Bobbys Barnacles" product "Big Barnacle"
    Then the product "Big Barnacle" is displayed
    When "Bobby" accesses another business called "Bellas Burgers" product "Bass Burger"
    Then the product "Bass Burger" is not displayed

  Scenario: AC2 - Adding items to catalogue.
    Given a user with name "Amy" has a "Retail Trade" business "Ancient Antiques" in "Australia"
    When "Amy" has a product "Arty Artichokes" to sell at business "Ancient Antiques"
    Then the business "Ancient Antiques" catalogue shows the product "Arty Artichokes"
#  AC2: I can add items to my catalogue.  For now, this is done via an appropriate form—later
#  we may support bulk loading.  Each catalogue entry has a mandatory product code.
#  Product codes are unique and provide a conveniently short way to identify products.
#  Teams may decide whether to allow users to choose product codes (e.g. for mnemonic reasons)
#  or whether codes should be automatically generated.

#  AC3: Catalogue items have additional fields: full name (mandatory), description, manufacturer,
#  recommended retail price (RRP).  The date a product is added to the catalogue is automatically recorded.

#  AC4: The currency (both the symbol and the currency code) is shown automatically
#  for RRP (and any other prices in the app), e.g. “$30 NZD”. The creator’s location determines the currency.
#  Use an API such as: https://restcountries.eu/ to be current.

#  AC5: Catalogue entries are displayed in an appropriate (probably tabular) form. There is a default ordering.
#  A combo box allows the viewer to reorder the products (e.g. by product code, by name,
#  by creation date, by price,  …).
