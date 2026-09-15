package seng202.team4.models;


import java.time.LocalDateTime;

public class Note {

    private int id;
    private String name;
    private LocalDateTime timeCreated;
    private int treeId;
    private String description;
    private String imagePath;

    public Note(int id, String name, LocalDateTime timeCreated, int treeId, String description, String imagePath) {
        this.id = id;
        this.name = name;
        this.timeCreated = timeCreated;
        this.treeId = treeId;
        this.description = description;
        this.imagePath = imagePath;
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

    public String getDescription() {
        return description;
    }

    public String getImagePath() {
        return imagePath;
    }




}
