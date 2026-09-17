Feature: Add note to journal
Scenario: Add a note to the journal
  Given a tree is selected on the map
  When the user enters the field note "Test note"
  Then the journal should have a note with description "Test note"

Scenario: Add a note to the journal with a photo
    Given a tree is selected on the map
    When the user adds a photo
    And the user enters the field note "Test note"
    Then the journal should have a note that says "Test note"
    And the journal note should have the photo
