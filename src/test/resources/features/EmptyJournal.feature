Feature: Empty journal AT_15 (not the displayed message) TODO

Scenario: Retrieve an empty journal
    Given no journal notes have been saved
    When the journal notes are requested
    Then no journal notes are returned