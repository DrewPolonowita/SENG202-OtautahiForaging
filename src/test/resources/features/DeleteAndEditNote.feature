Feature: Managing journal notes AT_21 and AT_22 TODO Check

Background:
    Given a tree is selected on the map
    And a note exists with description "Original note"

Scenario: AT_21 - Edit a note
    When the user updates the note description to "Updated note"
    Then the journal should have a note with description "Updated note"
    And the journal shouldn't have a note with description "Original note"

Scenario: AT_22 - Delete a note
    When the user deletes the note
    Then the journal shouldn't have a note with description "Original note"