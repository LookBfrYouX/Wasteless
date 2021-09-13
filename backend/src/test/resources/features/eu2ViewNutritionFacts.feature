#As a potential customer, I can see nutrition facts and allergy information of a listing.
Feature: EU2 - View Nutrition Facts Information
#
#AC1: I can view the nutritional facts of a product on the listing's detail and product detail page.
  Scenario: AC1 - Viewing the nutritional facts of a product on the listings and product pages
    Given There is a product "some product" with a "HIGH" level of fat and a "LOW" level of sugar
    When I request the product details
    Then I can see that the level of fat is "HIGH" and the level of sugar is "LOW"