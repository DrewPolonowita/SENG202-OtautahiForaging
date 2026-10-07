package seng202.team4.models;


import java.time.LocalDateTime;

/**
 * Model representing a note or journal entry
 */
public class Note {

    private int id;
    private String name;
    private LocalDateTime timeCreated;
    private int treeId;
    private String description;
    private String imagePath;
    private Integer weightGrams;

    /**
     * Constructor
     */
    public Note(int id, String name, LocalDateTime timeCreated, int treeId, String description, String imagePath, Integer weightGrams) {
        this.id = id;
        this.name = name;
        this.timeCreated = timeCreated;
        this.treeId = treeId;
        this.description = description;
        this.imagePath = imagePath;
        this.weightGrams = weightGrams;
    }

    /**
     * Getter for tree id
     * @return the id of a tree the note is about
     */
    public int getTreeId() {
        return treeId;
    }

    /**
     * Getter for name
     * @return the common name of the food that is grown on a tree for a given note
     */
    public String getName() {
        return name;
    }

    /**
     * Getter for time created
     * @return the time a note was created
     */
    public LocalDateTime getTimeCreated() {
        return timeCreated;
    }

    /**
     * Getter for a note id
     * @return the unique id for a note
     */
    public int getId() {
        return id;
    }

    /**
     * Getter for description
     * @return the description the user gave for a note
     */
    public String getDescription() {
        return description;
    }

    /**
     * Getter for image path
     * @return the image path for an image uploaded as part of a note
     */
    public String getImagePath() {
        return imagePath;
    }

    /**
     * Getter for fruit weight
     * @return the weight of x fruit in grams
     */
    public Integer getWeightGrams() {
        return weightGrams;
    }




}
