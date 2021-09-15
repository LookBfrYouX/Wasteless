Feature: EU3 Filter Sales Listings With Nutritional Information

  Scenario: AC3 - Filter by max nutri-score in listings search
    Given a user is logged in
    When I send a request to "/listings/search" with max nutri-score set to "B"
    Then only listings with a nutri-score of "A" and "B" are received

  Scenario: AC3 - Filter by min nutri-score in listings search
    Given a user is logged in
    When I send a request to "/listings/search" with min nutri-score group set to "D"
    Then only listings with a nutri-score of "D" and "E" are received

  Scenario: AC3 - Filter by nutri-score range in listings search
    Given a user is logged in
    When I send a request to "/listings/search" with min nutri-score set to "B" and max nutri-score set to "D"
    Then only listings with a NOVA group of "B", "C" and "D" are received
