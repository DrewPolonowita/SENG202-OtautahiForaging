package seng202.team4.services;

import seng202.team4.repository.NoteRepository;

public class NoteService {

    private final NoteRepository noteDAO;


    /**
     * Constructor
     */
    public NoteService(NoteRepository noteRepository) {
        this.noteDAO = noteRepository;
    }
}
