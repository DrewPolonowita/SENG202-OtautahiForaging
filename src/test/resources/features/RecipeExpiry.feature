Feature: FatSecret recipe data expiry, AT_25.1, AT_25.2

Scenario: Get a recipe with fresh data AT_25.1
    Given recipe 1234 has database data less than 24 hours old
    When the recipe is requested
    Then the recipe is returned from database
    And the api is not called

Scenario: Get a recipe with expired data AT_25.2
    Given recipe 123 has database data older than 24 hours
    And the recipe API provides new data
    When the recipe is requested
    Then the updated recipe is returned
    And the updated recipe is saved in database
