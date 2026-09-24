package seng202.team4.repository;

import seng202.team4.models.Note;

import java.util.List;

/**
 * Interface for the note specific data access object
 */
public interface NoteRepository extends DAOInterface<Note, Integer> {

    /**
     * Retrieve all notes linked to a specific tree
     * @param treeId the tree's id
     * @return all notes associated with that tree
     */
    List<Note> getNotesByTreeId(int treeId);


}
