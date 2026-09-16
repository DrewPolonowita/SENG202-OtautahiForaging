package seng202.team4.models;

/**
 * Model representation for a food
 * @author Team 4
 */
public class Food {
    private String name;
    private String type;
    private int id;
    private double calories;
    private double carbohydrates;
    private double protein;
    private double fat;
    private String servingDescription;
    private int servingId;


    /**
     * Constructor for food
     * @param name the name of the food
     * @param type the type of food (e.g. fruit or nut)
     */
    public Food(int id, String name, String type, double calories, double carbohydrates, double protein, double fat, String servingDescription, int servingId) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.calories = calories;
        this.carbohydrates = carbohydrates;
        this.protein = protein;
        this.fat = fat;
        this.servingDescription = servingDescription;
        this.servingId = servingId;

    }

    /**
     * Getter for food id
     * @return the unique identifier for a food model
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
     * Setter for a food name
     * @param name the name being given to food as a String
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter for type
     * @return the type of food for a given model as a String
     */
    public String getType() {
        return type;
    }

    /**
     * Setter for type
     * @param type the type of food the given model falls into (e.g. fruit or nut)
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Getter for calories
     * @return the calories in a serving for a food model
     */
    public double getCalories() {
        return calories;
    }

    /**
     * Getter for carbohydrates
     * @return the carbohydrates in a serving for a food model
     */
    public double getCarbohydrates() {
        return carbohydrates;
    }

    /**
     * Getter for protein
     * @return the protein in a serving for a food model
     */
    public double getProtein() {
        return protein;
    }

    /**
     * Getter for fat
     * @return the fat content in a serving for a food model
     */
    public double getFat() {
        return fat;
    }

    /**
     * Getter for serving description
     * @return the description of what a serving is for a food model
     */
    public String getServingDescription() {
        return servingDescription;
    }

    /**
     * Getter for serving id
     * @return unique identifier for a serving size
     */
    public int getServingId() {
        return servingId;
    }
}
