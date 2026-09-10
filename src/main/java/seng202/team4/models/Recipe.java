package seng202.team4.models;

import java.time.LocalDateTime;
import java.util.List;


/**
 * Model representation of a recipe
 * @author Team 4
 */
public class Recipe {
    private final int id;
    private final String name;
    private final String method;
    private final List<String> ingredients;
    private final double carbohydrates;
    private final double calories;
    private final double protein;
    private final double fat;
    private final String description;
    private final LocalDateTime retrievalTime;

    /**
     * Constructor for Recipe
     * @param id the recipe id from the API
     * @param name the name of the recipe
     * @param method the recipe method
     * @param ingredients ingredients needed for recipe
     * @param carbohydrates the carbohydrate content of the recipe
     * @param calories the calories of the recipe
     * @param protein the protein in the recipe
     * @param fat the fat in the recipe
     * @param description the description of what meal the recipe is producing
     * @param retrievalTime the time that the data was retrieved from the API
     */
    public Recipe(int id, String name, String method, List<String> ingredients, double carbohydrates, double calories, double protein, double fat, String description, LocalDateTime retrievalTime) {
        this.id = id;
        this.name = name;
        this.method = method;
        this.ingredients = ingredients;
        this.carbohydrates = carbohydrates;
        this.calories = calories;
        this.protein = protein;
        this.fat = fat;
        this.description = description;
        this.retrievalTime = retrievalTime;
    }

    /**
     * Getter for recipe id
     * @return recipe id as an integer
     */
    public int getId() {
        return id;
    }

    /**
     * Getter for recipe name
     * @return rrecipe name as String
     */
    public String getName() {
        return name;
    }

    /**
     * Getter for recipe method
     * @return the recipe method as a String
     */
    public String getMethod() {
        return method;
    }

    /**
     * Getter for recipe ingredients
     * @return recipe ingredients as a list of strings
     */
    public List<String> getIngredients() {
        return ingredients;
    }

    /**
     * Getter for recipe carbohydrates
     * @return carbrohydrate content of a recipe as a double
     */
    public double getCarbohydrates() {
        return carbohydrates;
    }

    /**
     * Getter for recipe calories
     * @return calories of the recipe
     */
    public double getCalories() {
        return calories;
    }

    /**
     * Getter for recipe protein
     * @return recipe protein as a double
     */
    public double getProtein() {
        return protein;
    }

    /**
     * Getter for recipe fat
     * @return fat content of a recipe as a double
     */
    public double getFat() {
        return fat;
    }

    /**
     * Getter for recipe description
     * @return the description for a recipe as a String
     */
    public String getDescription() {
        return description;
    }

    public LocalDateTime getRetrievalTime() {
        return retrievalTime;
    }
}
