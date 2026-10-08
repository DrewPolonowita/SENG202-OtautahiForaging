package seng202.team4.cucumber;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import seng202.team4.models.Note;
import seng202.team4.repository.NoteRepository;
import seng202.team4.services.NoteService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EmptyJournalDefSteps {
    private NoteRepository mockNoteRepository;
    private NoteService noteService;
    private List<Note> result;

    @Before
    public void setUp() {
        mockNoteRepository = mock(NoteRepository.class);
        noteService = new NoteService(mockNoteRepository);
    }

    @Given("no journal notes have been saved")
    public void noJournalNotesSaved() {
        when(mockNoteRepository.getAll())
                .thenReturn(List.of());
    }

    @When("the journal notes are requested")
    public void journalNotesRequested() {
        result = noteService.getAll();
    }

    @Then("no journal notes are returned")
    public void noJournalNotesReturned() {
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(mockNoteRepository).getAll();
    }
}
