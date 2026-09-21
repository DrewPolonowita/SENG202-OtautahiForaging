package seng202.team4.services;

import seng202.team4.exceptions.DuplicateEntryException;
import seng202.team4.models.Note;
import seng202.team4.repository.NoteRepository;

import java.util.List;

/**
 * Service class for handling business logic for managing notes
 */
public class NoteService {

    private final NoteRepository noteRepository;


    /**
     * Constructor
     * @param noteRepository the repository used to access note data
     */
    public NoteService(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    /**
     * Returns all notes in the database
     * @return list of all notes
     */
    public List<Note> getAll() {
        return noteRepository.getAll();
    }

    /**
     * Adds a note to the database
     * @param note the note to add
     * @return id of the newly created note, or -1 if failure
     * @throws DuplicateEntryException if a note with a duplicate identifier already exists
     */
    public int add(Note note) throws DuplicateEntryException {
        return noteRepository.add(note);
    }

    /**
     * Returns all the notes linked to a specific tree
     * @param treeId the tree's id
     * @return list of notes associated with that tree
     */
    public List<Note> getNotesByTreeId(int treeId) {
        return noteRepository.getNotesByTreeId(treeId);
    }
}
