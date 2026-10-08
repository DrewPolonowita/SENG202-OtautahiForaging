Feature: Filter trees by type AT_4, AT_5 TODO: Update

Scenario: Deselect a tree type
    Given all the tree filters are selected
    When the user toggles the "Apple" filter
    Then the "Apple" filter is deselected
    And the other tree filters remain selected

Scenario: Select all tree filters
    Given none of the tree filters are selected
    When the user selects all the filters
    Then every tree filter should be selected


Scenario: Clear all tree filters
    Given all the tree filters are selected
    When the user clears all the filters
    Then all the tree filters are deselected