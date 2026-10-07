package seng202.team4.models;

/**
 * Represents a serving of food from the database
 */
public class FoodServing {
    private final int servingId;
    private final String description;
    private final double calories;
    private final double carbohydrates;
    private final double protein;
    private final double fat;

    /**
     * Constructor
     * @param servingId serving id
     * @param description description (e.g 100 g)
     * @param calories calories in kcal
     * @param carbohydrates carbohydrates in grams
     * @param protein protein in grams
     * @param fat fat in grams
     */
    public FoodServing(int servingId, String description, double calories,
                       double carbohydrates, double protein, double fat) {
        this.servingId = servingId;
        this.description = description;
        this.calories = calories;
        this.carbohydrates = carbohydrates;
        this.protein = protein;
        this.fat = fat;
    }

    public int getServingId() {
        return servingId;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Getter for calories
     * @return the calories in a servings for a food model
     */
    public double getCalories() {
        return calories;
    }

    /**
     * Getter for carbohydrates
     * @return the carbohydrates in a servings for a food model
     */
    public double getCarbohydrates() {
        return carbohydrates;
    }

    /**
     * Getter for protein
     * @return the protein in a servings for a food model
     */
    public double getProtein() {
        return protein;
    }

    /**
     * Getter for fat
     * @return the fat content in a servings for a food model
     */
    public double getFat() {
        return fat;
    }
}
