Feature: U15 Product Catalogue

  Scenario: AC1 - Accessing the catalogue.
    Given a user with name "Bobby" is logged in and administers a business called "Bobbys Barnacles"
    When "Bobby" accesses "Bobbys Barnacles" product "Big Barnacle"
    Then the product "Big Barnacle" is displayed
    When "Bobby" accesses another business called "Bellas Burgers" product "Bass Burger"
    Then the product "Bass Burger" is not displayed
