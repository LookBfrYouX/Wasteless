Feature: U31 Purchases

#  Note: It was agreed with the product owner that in taking on User Story 31 we would not
#  complete the notification feature of the story. Thus, AC1 and AC2 are not tested as they cover
#  the criteria of the notification feature.

#  AC3: The seller’s inventory is updated to reflect the fact that the goods sold are no longer
#  in stock.
  Scenario: AC3 - On listing purchase, inventory-item is updated
    Given A business owner has a business named "Save and Pack"
    And the business has a product "Orange Juice" with price 3.99 and manufacturer "We make OJ"
    And the business has 6 of them with a total price of 23.00
    And there are 2 listings for 3 of the products with a price of 11.40 for each listing
    When A user purchases one of the listings
    Then the corresponding inventory-item's quantity decreases by the correct amount

#  AC4: The sale listing is removed and will not appear in future searches (see U29).
  Scenario: AC4 - On listing purchase, listing is removed
    Given A business owner has a business named "Old World"
    And the business has a product "Apple Juice" with price 3.99 and manufacturer "We make AJ"
    And the business has 6 of them with a total price of 23.00
    And there are 2 listings for 3 of the products with a price of 11.40 for each listing
    When A user purchases one of the listings
    Then the inventory-item's listing no longer exists

#  AC5: Information about the sale (e.g. sale date, listing date, product, amount, number of likes, …)
#  is recorded in a sales history for the seller’s business.
#  Note: in future stories we may wish to analyse this information to help businesses to understand and improve their processes.
  Scenario: AC5 - On listing purchase, transaction is created
    Given A business owner has a business named "Count Up"
    And the business has a product "Pineapple Juice" with price 3.99 and manufacturer "We make PJ"
    And the business has 6 of them with a total price of 23.00
    And there are 2 listings for 3 of the products with a price of 11.40 for each listing
    When A user purchases one of the listings
    Then A transaction is created with a valid Id