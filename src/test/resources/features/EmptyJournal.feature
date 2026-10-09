Feature: Empty journal AT_14 (not the displayed message)

Scenario: Retrieve an empty journal AT_14
    Given no journal notes have been saved
    When the journal notes are requested
    Then no journal notes are returned