package seng202.team4.cucumber;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import seng202.team4.exceptions.DuplicateEntryException;
import seng202.team4.models.Note;
import seng202.team4.models.TreeModel;
import seng202.team4.repository.DatabaseManager;
import seng202.team4.repository.NoteDAO;
import seng202.team4.repository.TreeDAO;
import seng202.team4.services.NoteService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class AddNoteToJournalStepDefs {
    private static final String TEST_DB = "jdbc:sqlite:build/test-database.db";

    private DatabaseManager databaseManager;
    private TreeDAO treeDAO;
    private NoteService noteService;


    private int treeId;
    private String treeName;
    private String photoPath;
    private Note testNote;

    @Before
    public void setUp() throws IOException {
        Files.deleteIfExists(Path.of("build/test-database.db"));

        databaseManager = new DatabaseManager(TEST_DB);
        treeDAO = new TreeDAO(databaseManager);
        noteService = new NoteService(new NoteDAO(databaseManager));
    }

    @After
    public void tearDown() throws IOException {
        Files.deleteIfExists(Path.of("build/test-database.db"));
    }

    @Given("a tree is selected on the map")
    public void aTreeIsSelectedOnTheMap() throws DuplicateEntryException {
        treeId = 2452;
        treeName = "Apple";

        TreeModel tree = new TreeModel(
                treeId,
                List.of(5252f, 3423f),
                "Council",
                "Malus",
                "Apple",
                treeName,
                null,
                "serviced"
        );

        treeDAO.add(tree);

    }


    @When("the user adds a photo")
    public void theUserAddsAPhoto() {

        photoPath = "test-photo.jpg";
    }

    @When("the user enters the field note {string}")
    public void theUserEntersTheFieldNote(String description) throws DuplicateEntryException {
        Note note = new Note(
                0,
                treeName,
                null,
                treeId,
                description,
                photoPath,
                0
        );

        noteService.add(note);
    }

    @Then("the journal should have a note with description {string}")
    public void theJournalShouldHaveANoteWithDescription(String expectedDescription) {
        testNote = findNoteByDescription(expectedDescription);
        Assertions.assertEquals(expectedDescription, testNote.getDescription());
        Assertions.assertEquals(treeId, testNote.getTreeId());
    }

    @Then("the journal should have a note that says {string}")
    public void theJournalShouldHaveANoteThatSays(String expectedDescription) {
        theJournalShouldHaveANoteWithDescription(expectedDescription);
    }

    @Then("the journal note should have the photo")
    public void theJournalNoteShouldHaveThePhoto() {
        Assertions.assertEquals(photoPath, testNote.getImagePath());
    }

    @Then("the journal should be linked to the chosen tree")
    public void theJournalLinkedToTheChosenTree() {
        Assertions.assertNotNull(testNote);
        Assertions.assertEquals(treeId, testNote.getTreeId());
    }

    private Note findNoteByDescription(String description) {
        List<Note> notes = noteService.getNotesByTreeId(treeId);
        for (Note note : notes) {
            if (description.equals(note.getDescription())) {
                return note;
            }
        }
        return null;
    }
}