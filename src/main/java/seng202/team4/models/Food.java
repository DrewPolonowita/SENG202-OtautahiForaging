package seng202.team4.models;


/**
 * Model of food for storage in database and use in the application
 * Can't be stored for more than 24 hours
 * Nutritional information is based off the default serving, or the first serving returned by the API record
 * @author Team 4
 */
public class Food {
    private final int foodId;
    private String name;
    private String type;
    private final int servingId;
    private final String servingDescription;
    private final double calories;
    private final double carbohydrates;
    private final double protein;
    private final double fat;
    private final String nutritionalInformation;

    /**
     * Constructor
     * @param foodId id of food - information can be stored
     * @param type type of food by the database
     * @param servingId id of the serving we use in the API for our nutritional information
     * @param servingDescription description of the serving we use, not necessarily displayed in app
     * @param calories calories in kcal
     * @param carbohydrates total carbohydrates in grams
     * @param protein total protein in grams
     * @param fat total fat content in grams
     * @param nutritionalInformation - Temporary
     */
    public Food(
            int foodId,
            String name,
            String type,
            int servingId,
            String servingDescription,
            double calories,
            double carbohydrates,
            double protein,
            double fat,
            String nutritionalInformation // temp
    ) {
        this.foodId = foodId;
        this.name = name;
        this.type = type;
        this.servingId = servingId;
        this.servingDescription = servingDescription;
        this.calories = calories;
        this.carbohydrates = carbohydrates;
        this.protein = protein;
        this.fat = fat;
        this.nutritionalInformation = nutritionalInformation;  // temp
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNutritionalInformation() {
        return nutritionalInformation; }
}
