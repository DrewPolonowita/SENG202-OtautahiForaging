Feature: Add note to journal: AT_12, AT_13 TODO CHECK
Scenario: Add a note to the journal
    Given a tree is selected on the map
    When the user enters the field note "Test note"
    Then the journal should have a note with description "Test note"
    And the journal should be linked to the chosen tree
Scenario: Add a note to the journal with a photo
    Given a tree is selected on the map
    When the user adds a photo
    And the user enters the field note "Test note"
    Then the journal should have a note that says "Test note"
    And the journal note should have the photo
    And the journal should be linked to the chosen tree
