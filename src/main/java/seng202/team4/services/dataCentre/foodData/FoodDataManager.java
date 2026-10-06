package seng202.team4.services.dataCentre.foodData;

import seng202.team4.models.Food;

/**
 * Interface for interacting with food data, abstracting away the api vs database details
 */
public interface FoodDataManager {

    /**
     * Method to get a specific food using the food name
     * @param foodName food to look for
     * @return {@link Food} object
     */
    Food getFood(String foodName);

}
