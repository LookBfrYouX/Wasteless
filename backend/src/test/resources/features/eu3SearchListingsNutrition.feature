Feature: EU3 Filter Sales Listings With Nutritional Information

  Scenario: AC3 -  Filter max NOVA group in listings search
    Given a user is logged in
    When I send a request to "/listings/search" with max NOVA group set to 2
    Then only listings with a NOVA group of 1 and 2 are received

  Scenario: AC3 -  Filter min NOVA group in listings search
    Given a user is logged in
    When I send a request to "/listings/search" with min NOVA group set to 3
    Then only listings with a NOVA group of 3 and 4 are received

  Scenario: AC3 -  Filter range NOVA group in listings search
    Given a user is logged in
    When I send a request to "/listings/search" with min NOVA group set to 2 and max nova group set to 3
    Then only listings with a NOVA group of 2 and 3 are received