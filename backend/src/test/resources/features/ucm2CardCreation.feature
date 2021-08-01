Feature: UCM2 Card Creation

  Scenario: Before all
    Given There is a user
      | emailAddress            | password | firstName | lastName | nickName |
      | testUser2@wasteless.com | t3stUser | Test      | User     | TestyBoi |

  Scenario: AC1 - Create a card for one of the three sections
    Given A user is logged in with email "testUser2@wasteless.com" and password "t3stUser"
    When I create a card "Test Card" for a section "Exchange"
    Then The card "Test Card" is created in "Exchange" section and stored
