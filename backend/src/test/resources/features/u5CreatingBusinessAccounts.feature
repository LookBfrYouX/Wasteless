Feature: U5 Creating Business Accounts

  Scenario: Before all
    Given this user exists
      | emailAddress                  | password | firstName | lastName | nickName |
      | testUser1111111@wasteless.com | t3stUser | Test      | User     | TestyBoi |

  Scenario: AC1 - Registering a business
    Given I am logged in with email "testUser1111111@wasteless.com" and password "t3stUser"
    When I create a "Retail Trade" business "testBusiness1"
    Then The business "testBusiness1" is created and stored

  Scenario: AC2 - Ensuring required attributes exist
    Given I am logged in with email "testUser1111111@wasteless.com" and password "t3stUser"
    When I create a business "testBusiness2" without the required field: BusinessType
    Then The business is not created and an error is thrown

  Scenario: AC3 - Ensuring businesses can only be of 4 types
  (Accommodation and Food Services, Retail Trade, Charitable organisation, Non-profit organisation)
    Given I am logged in with email "testUser1111111@wasteless.com" and password "t3stUser"
    When I create an illegitimate "Retail" business "testBusiness3"
    Then The business is not created and a 400 error is thrown

  Scenario: AC5.1 - Primary administrator access given to creator
    Given I am logged in with email "testUser1111111@wasteless.com" and password "t3stUser"
    When I create a legitimate "Retail Trade" business "testBusiness4"
    Then I can see myself as the primary administrator

  Scenario: AC5.2 - Primary administrator can grant access to other individual users
    Given A user exists with email "tony@gmail.com" and password "t3stUser"
    Given I am logged in with email "testUser1111111@wasteless.com" and password "t3stUser"
    When I create a legitimate "Retail Trade" business "testBusiness5"
    When I set this user as an admin of my newly created business
    Then I can see him in the list of admins for the business

  Scenario: AC5.3 - Primary administrator can revoke access from other users
    Given A user exists with email "tony@gmail.com" and password "t3stUser"
    Given I am logged in with email "testUser1111111@wasteless.com" and password "t3stUser"
    When I create a legitimate "Retail Trade" business "testBusiness5"
    When I set this user as an admin of my newly created business
    Then I can see him in the list of admins for the business
    Then I can remove him from the list of admins for my business
    Then I can see he is not in the list of admins for my business

  Scenario: AC5.4 - Primary administrator cannot revoke their own admin status
    Given A user exists with email "tony@gmail.com" and password "t3stUser"
    Given I am logged in with email "testUser1111111@wasteless.com" and password "t3stUser"
    When I create a legitimate "Retail Trade" business "testBusiness5"
    Then I cannot remove myself from this list of admins