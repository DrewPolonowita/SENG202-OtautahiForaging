package seng202.team4.models;


import java.time.LocalDateTime;

public class Note {

    private int id;
    private String name;
    private LocalDateTime timeCreated;

    public Note(int id, String name, LocalDateTime timeCreated) {
        this.id = id;
        this.name = name;
        this.timeCreated = timeCreated;
    }


}
