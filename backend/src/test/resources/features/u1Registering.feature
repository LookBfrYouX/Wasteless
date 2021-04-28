Feature: U1
  Background:
    Given I am not logged in

  Scenario: I log in with a valid username and password
    When I submit my credentials
    Then I am logged in
    And I am taken to my profile page

  Scenario: I log in with an invalid credentials
    When I submit my credentials
    Then I am shown an error that my username or password is not valid

  Scenario: I log in with no username
    When I submit my credentials
    Then I am prompted to enter and username

  Scenario: I log in with no password
    When I submit my credentials
    Then I am prompted to enter a password

  Scenario: The network is unavailable
    When I submit my credentials
    Then A message displays instructing me to try again later