package seng202.team4.unittests.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team4.models.Note;
import seng202.team4.models.TreeModel;
import seng202.team4.repository.DatabaseManager;
import seng202.team4.repository.NoteDAO;
import seng202.team4.repository.TreeDAO;
import seng202.team4.unittests.TestSuite;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for NoteDAO verifying note creation, retrieval, and deletion.
 * @author Team 4
 */
public class NoteDAOTest extends TestSuite {

    private NoteDAO noteDAO;

    /**
     * Initializes a NoteDAO backed by the test database before each test
     */
    @BeforeEach
    void setUp() {
        DatabaseManager databaseManager = new DatabaseManager(this.getTestDb());
        noteDAO = new NoteDAO(databaseManager);
    }

    /**
     * Verifies that a note added to the database can be retrieved with matching fields
     */
    @Test
    void testAddNote() {
        Note toAdd = new Note(0, "Test Tree", null, 1298, "yo morgan", null);

        int newId = noteDAO.add(toAdd);
        assertTrue(newId > 0);

        Note retrieved = noteDAO.getOne(newId);
        assertNotNull(retrieved);
        assertEquals("Test Tree", retrieved.getName());
        assertEquals(1298, retrieved.getTreeId());
        assertEquals("yo morgan", retrieved.getDescription());
    }

    /**
     * Verifies that adding a note with invalid (null required-field) data fails gracefully,
     * returning -1 rather than throwing
     */
    @Test
    void testFailsBadInput() {
        Note invalid = new Note(0, null, null, 1298, null, null);

        int result = noteDAO.add(invalid);
        assertEquals(-1, result);
    }

    /**
     * Verifies that a deleted note is no longer retrievable
     */
    @Test
    void testDeleteNote() {
        Note toAdd = new Note(0, "doomed Tree", null, 5678, null, null);
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
    void testGetAll() {
        assertTrue(noteDAO.getAll().isEmpty());

        int id1 = noteDAO.add(new Note(0, "Tree A", null, 1, null, null));
        int id2 = noteDAO.add(new Note(0, "Tree B", null, 2, null, null));
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
    void testNoteTreeLookup() {
        TreeDAO treeDAO = new TreeDAO(new DatabaseManager(getTestDb()));

        TreeModel tree1 = new TreeModel(101, new float[]{172.60f, -43.50f},
                "Council", "Malus", "Apple", "Apple", null, "serviced");
        TreeModel tree2 = new TreeModel(202, new float[]{172.65f, -43.55f},
                "Council", "Pyrus", "Pear", "Pear", null, "serviced");
        treeDAO.add(tree1);
        treeDAO.add(tree2);

        int noteId = noteDAO.add(new Note(0, "Pear", null, 202, "wow i really like pears", null));
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
     * Verifies that a note remains accessible after its linked tree is deleted,
     * and that looking up the tree returns null.
     */
    @Test
    void testDeletedNoteTree() {
        TreeDAO treeDAO = new TreeDAO(new DatabaseManager(getTestDb()));
        TreeModel tree = new TreeModel(202, new float[]{172.65f, -43.55f},
                "Council", "Pyrus", "Pear", "Pear", null, "serviced");
        treeDAO.add(tree);
        int noteId = noteDAO.add(new Note(0, "Pear", null, 202, "wow i really like pears", null));
        assertTrue(noteId > 0);
        treeDAO.delete(tree.getId());
        Note savedNote = noteDAO.getOne(noteId);
        assertNotNull(savedNote);

        TreeModel resolvedTree = treeDAO.getOne(savedNote.getTreeId());
        assertNull(resolvedTree);
    }
}