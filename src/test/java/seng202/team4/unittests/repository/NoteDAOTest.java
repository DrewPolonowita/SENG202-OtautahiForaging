package seng202.team4.unittests.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team4.exceptions.DatabaseAccessException;
import seng202.team4.exceptions.DuplicateEntryException;
import seng202.team4.models.Note;
import seng202.team4.models.TreeModel;
import seng202.team4.repository.DatabaseManager;
import seng202.team4.repository.NoteDAO;
import seng202.team4.repository.NoteRepository;
import seng202.team4.repository.TreeDAO;
import seng202.team4.repository.TreeRepository;
import seng202.team4.unittests.TestSuite;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for NoteDAO verifying note creation, retrieval, and deletion.
 * @author Team 4
 */
class NoteDAOTest extends TestSuite {

    private NoteRepository noteDAO;
    private TreeRepository treeDAO;

    /**
     * Initializes a NoteDAO backed by the test database before each test
     */
    @BeforeEach
    void setUp() {
        DatabaseManager databaseManager = new DatabaseManager(this.getTestDb());
        noteDAO = new NoteDAO(databaseManager);
        treeDAO = new TreeDAO(databaseManager);
    }

    /**
     * Verifies that a note added to the database can be retrieved with matching fields
     */
    @Test
    void testAddNote() throws DuplicateEntryException {
        addTree(1298);
        Note toAdd = new Note(0, "Test Tree", null, 1298, "yo morgan", null, 0);

        int newId = noteDAO.add(toAdd);
        assertTrue(newId > 0);

        Note retrieved = noteDAO.getOne(newId);
        assertNotNull(retrieved);
        assertEquals("Test Tree", retrieved.getName());
        assertEquals(1298, retrieved.getTreeId());
        assertEquals("yo morgan", retrieved.getDescription());
    }

    /**
     * Verifies that updating the description of a note persists
     * using a fresh database read to ensure persistence rather than just comparing.
     */
    @Test
    void testUpdateDescPersists() throws DuplicateEntryException{
        addTree(1298);
        Note toAdd = new Note(0, "Epic tree", null, 1298, "original description", null, 0);
        int originalId = noteDAO.add(toAdd);
        assertTrue(originalId>0);

        Note retrieved = noteDAO.getOne(originalId);
        assertNotNull(retrieved);

        Note updatedNote = new Note(originalId, retrieved.getName(),
                retrieved.getTimeCreated(), retrieved.getTreeId(),
                "updated description", retrieved.getImagePath(), retrieved.getWeightGrams());

        noteDAO.update(updatedNote);

        Note afterUpdate = noteDAO.getOne(originalId);
        assertNotNull(afterUpdate);
        assertEquals("updated description", afterUpdate.getDescription());
        assertEquals(retrieved.getTimeCreated(), afterUpdate.getTimeCreated());
    }

    /**
     * Verifies that updating the image of a note persists,
     * using a fresh database read to ensure persistence rather than just comparing.
     */
    @Test
    void testUpdatePhotoPersists() throws DuplicateEntryException{
        addTree(1298);
        Note toAdd = new Note(0, "Epic tree", null, 1298, "description", "original-image.png", 0);
        int originalId = noteDAO.add(toAdd);
        assertTrue(originalId>0);

        Note retrieved = noteDAO.getOne(originalId);
        assertNotNull(retrieved);

        Note updatedNote = new Note(originalId, retrieved.getName(),
                retrieved.getTimeCreated(), retrieved.getTreeId(),
                retrieved.getDescription(), "updated-image.png", retrieved.getWeightGrams());

        noteDAO.update(updatedNote);

        Note afterUpdate = noteDAO.getOne(originalId);
        assertNotNull(afterUpdate);
        assertEquals("updated-image.png", afterUpdate.getImagePath());
    }

    /**
     * Verifies that updating one note doesn't involuntarily alter another note,
     * catches an update possibly changing other rows in the database.
     */
    @Test
    void testUpdateLeavesOtherNotesUnchanged() throws DuplicateEntryException {
        addTree(1298);
        int firstId = noteDAO.add(new Note(
                0, "Epic tree", null, 1298, "first description", null, 0));
        int secondId = noteDAO.add(new Note(
                0, "Epic tree", null, 1298, "second description", null, 0));
        assertTrue(firstId > 0);
        assertTrue(secondId > 0);
        Note first = noteDAO.getOne(firstId);
        assertNotNull(first);
        noteDAO.update(new Note(firstId, first.getName(), first.getTimeCreated(),
                first.getTreeId(), "updated description", first.getImagePath(), 0));
        Note updated = noteDAO.getOne(firstId);
        Note unchanged = noteDAO.getOne(secondId);
        assertNotNull(updated);
        assertNotNull(unchanged);
        assertEquals("updated description", updated.getDescription());
        assertEquals("second description", unchanged.getDescription());
    }

    /**
     * Verifies that removing a note's photo persists a null image path
     * while preserving its attributes like description.
     */
    @Test
    void testRemovePhotoPersists() throws DuplicateEntryException {
        addTree(1298);
        int testId = noteDAO.add(new Note(
                0, "cool tree", null, 1298, "description", "image.png", 0));
        assertTrue(testId > 0);
        Note test = noteDAO.getOne(testId);
        assertNotNull(test);
        assertEquals("image.png", test.getImagePath());
        noteDAO.update(new Note(testId, test.getName(), test.getTimeCreated(), test.getTreeId(), test.getDescription(), null, 0));
        Note afterUpdate = noteDAO.getOne(testId);
        assertNotNull(afterUpdate);
        assertNull(afterUpdate.getImagePath());
        assertEquals(test.getDescription(), afterUpdate.getDescription());
    }

    /**
     * Verifies that adding a note with invalid (null required-field) data fails gracefully,
     * returning -1 rather than throwing
     */
    @Test
    void testFailsBadInput() throws DuplicateEntryException{
        addTree(1298);
        Note invalid = new Note(0, null, null, 1298, null, null, 0);

        assertThrows(DatabaseAccessException.class, () -> noteDAO.add(invalid));
    }

    /**
     * Verifies that a deleted note is no longer retrievable
     */
    @Test
    void testDeleteNote() throws DuplicateEntryException {
        addTree(5678);
        Note toAdd = new Note(0, "doomed Tree", null, 5678, null, null, 0);
        int newId = noteDAO.add(toAdd);
        assertTrue(newId > 0);

        noteDAO.delete(newId);

        Note afterDelete = noteDAO.getOne(newId);
        assertNull(afterDelete);
    }

    /**
     * Verifies that getAll reflects notes as they are added and deleted,
     * returning an empty list initially and updating correctly after each change
     */
    @Test
    void testGetAll() throws DuplicateEntryException {
        assertTrue(noteDAO.getAll().isEmpty());

        addTree(1);
        addTree(2);
        int id1 = noteDAO.add(new Note(0, "Tree A", null, 1, null, null, 0));
        int id2 = noteDAO.add(new Note(0, "Tree B", null, 2, null, null, 0));
        assertEquals(2, noteDAO.getAll().size());

        noteDAO.delete(id1);
        List<Note> remaining = noteDAO.getAll();
        assertEquals(1, remaining.size());
        assertEquals(id2, remaining.get(0).getId());
    }

    /**
     * Verifies that a saved note retrieves its linked tree,
     * including the correct ID and coordinates.
     */
    @Test
    void testNoteTreeLookup() throws DuplicateEntryException {
        TreeModel tree1 = new TreeModel(101, List.of(172.60f, -43.50f),
                "Council", "Malus", "Apple", "Apple", null, "serviced");
        TreeModel tree2 = new TreeModel(202, List.of(172.65f, -43.55f),
                "Council", "Pyrus", "Pear", "Pear", null, "serviced");
        treeDAO.add(tree1);
        treeDAO.add(tree2);

        int noteId = noteDAO.add(new Note(0, "Pear", null, 202, "wow i really like pears", null, 0));
        assertTrue(noteId > 0); // saved successful

        Note savedNote = noteDAO.getOne(noteId);
        assertNotNull(savedNote);

        TreeModel resolvedTree = treeDAO.getOne(savedNote.getTreeId());
        assertNotNull(resolvedTree);
        assertEquals(202, resolvedTree.getId());
        assertEquals(172.65, resolvedTree.getXCoord(), 0.0001);
        assertEquals(-43.55, resolvedTree.getYCoord(), 0.0001);
    }

    /**
     * Verifies that a tree referenced by a note cannot be deleted,
     * preserving the note's link to the tree.
     */
    @Test
    void testReferencedTreeNotDeleted() throws DuplicateEntryException {
        TreeModel tree = new TreeModel(202,List.of(172.60f, -43.55f),
                "Council", "Pyrus", "Pear", "Pear", null, "serviced");
        treeDAO.add(tree);
        int noteId = noteDAO.add(new Note(0, "Pear", null, tree.getId(), "wow i really like pears", null, 0));
        assertTrue(noteId > 0);
        treeDAO.delete(tree.getId());
        Note savedNote = noteDAO.getOne(noteId);
        assertNotNull(savedNote);

        TreeModel resolvedTree = treeDAO.getOne(savedNote.getTreeId());
        assertNotNull(resolvedTree);
        assertEquals(tree.getId(), resolvedTree.getId());
    }

    private void addTree(int id) throws DuplicateEntryException {
        treeDAO.add(new TreeModel(
                id,
                List.of(1.0f, 1.0f),
                "CCC",
                "Malus",
                "Apple",
                "Apple",
                LocalDateTime.now(),
                ""
        ));
    }
}