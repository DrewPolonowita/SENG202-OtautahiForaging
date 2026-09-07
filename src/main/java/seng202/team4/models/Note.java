package seng202.team4.models;


import java.time.LocalDateTime;

public class Note {

    private int id;
    private String name;
    private LocalDateTime timeCreated;
    private int treeId;

    public Note(int id, String name, LocalDateTime timeCreated, int treeId) {
        this.id = id;
        this.name = name;
        this.timeCreated = timeCreated;
        this.treeId = treeId;
    }

    public int getTreeId() {
        return treeId;
    }

    public String getName() {
        return name;
    }

    public LocalDateTime getTimeCreated() {
        return timeCreated;
    }

    public int getId() {
        return id;
    }




}
