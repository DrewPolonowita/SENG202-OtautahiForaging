Feature: Managing journal notes AT_18 and AT_19

Background:
    Given a tree is selected on the map
    And a note exists with description "Original note"

Scenario: Edit a note AT_18
    When the user updates the note description to "Updated note"
    Then the journal should have a note with description "Updated note"
    And the journal shouldn't have a note with description "Original note"

Scenario: Delete a note AT_19
    When the user deletes the note
    Then the journal shouldn't have a note with description "Original note"