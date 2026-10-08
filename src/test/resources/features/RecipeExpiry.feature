Feature: FatSecret recipe data expiry, AT_23 TODO: UPDATE THIS AT_X

Scenario: Get a recipe with fresh data
    Given recipe 1234 has database data less than 24 hours old
    When the recipe is requested
    Then the recipe is returned from database
    And the api is not called

Scenario: Get a recipe with expired data
    Given recipe 123 has database data older than 24 hours
    And the recipe API provides new data
    When the recipe is requested
    Then the updated recipe is returned
    And the updated recipe is saved in database
