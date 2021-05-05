Feature: U5 Creating Business Accounts

  Scenario: Before all
    Given this user exists
      | emailAddress                  | password | firstName | lastName | nickName |
      | testUser1111111@wasteless.com | t3stUser | Test      | User     | TestyBoi |

  Scenario: AC1 - Registering a business
    When I am logged in with email "testUser1111111@wasteless.com" and password "t3stUser"
    Given I create a "Retail Trade" business "testBusiness1"
    Then The business "testBusiness1" is created and stored

  Scenario: AC2 - Ensuring required attributes exist
    When I am logged in with email "testUser1111111@wasteless.com" and password "t3stUser"
    Given I create a business "testBusiness2" without the required field: BusinessType
    Then The business is not created and an error is thrown

  Scenario: AC3 - Ensuring businesses can only be of 4 types
  (Accommodation and Food Services, Retail Trade, Charitable organisation, Non-profit organisation)
    When I am logged in with email "testUser1111111@wasteless.com" and password "t3stUser"
    Given I create an illegitimate "Retail" business "testBusiness3"
    Then The business is not created and a 400 error is thrown

  Scenario: AC5 - Primary administrator access given to creator
    When I am logged in with email "testUser1111111@wasteless.com" and password "t3stUser"
    Given I create a legitimate "Retail Trade" business "testBusiness4"
    Then I can see myself as the primary administrator