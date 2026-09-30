package seng202.team4.models;


import java.util.List;

/**
 * Model representation for a food
 * @author Team 4
 */
public class Food {
    private String name;
    private String type;
    private int id;
    private List<FoodServing> servings;


    /**
     * Constructor for food
     * @param name the name of the food
     * @param type the type of food (e.g. fruit or nut)
     */
    public Food(int id, String name, String type, List<FoodServing> servings) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.servings = servings;
    }

    /**
     * Getter for food id
     * @return the  unique identifier for a food model
     */
    public int getId() {
        return id;
    }

    /**
     * Getter for food name
     * @return food name as a String
     */
    public String getName() {
        return name;
    }

    /**
     * Getter for type
     * @return the type of food for a given model as a String
     */
    public String getType() {
        return type;
    }

    public List<FoodServing> getServings() {
        return servings;
    }

    public FoodServing getDefaultServing() {
        return servings.getFirst();
    }

}
