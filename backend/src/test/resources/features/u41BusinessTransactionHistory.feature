Feature: U41 Business Transaction History

  Background:
    Given a user with username "dnb36@uclive.ac.nz" and password "fun123" is logged in
    And Given they are managing a business called "TestName"

  Scenario: AC3 - Setting Custom Period, Dates Not Out of Bounds
    When I send a request to the transaction endpoint with a start date of "2020-01-02", end date of "2021-03-01" and granularity of ""
    Then The returned dates are between "2020-01-02" and "2021-03-01" inclusive

  Scenario: AC3 - Setting Custom Period, Right Dates Returned
    When I send a request to the transaction endpoint with a start date of "2021-01-02", end date of "2021-01-01" and granularity of ""
    Then The returned dates are between "2021-01-02" and "2021-01-02" inclusive

  Scenario: AC4 - Setting Granularity to MONTH
    When I send a request to the transaction endpoint with a start date of "", end date of "" and granularity of "MONTH"
    Then There is only one entry per month

  Scenario: AC3 and AC4 - Setting Custom Period, Granularity to MONTH, Dates Not Out of Bounds
    When I send a request to the transaction endpoint with a start date of "2020-01-24", end date of "2021-03-10" and granularity of "MONTH"
    Then The returned dates are between "2021-02-01" and "2021-02-28" inclusive

