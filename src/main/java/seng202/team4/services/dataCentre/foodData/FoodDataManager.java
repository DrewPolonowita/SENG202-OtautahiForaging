package seng202.team4.services.dataCentre.foodData;

import seng202.team4.models.Food;

import java.util.List;

/**
 * Interface for interacting with food data, abstracting away the api vs database details
 */
public interface FoodDataManager {

    /**
     * Method to get all the foods we have mapped for the trees with api data
     * First searches the FatSecret API for food ids using each food map key (foodName)
     * and then uses those ids to search by specific id
     * @return List of food objects that exist in the project
     */
    List<Food> getFoods();

    Food getFood(String foodName);

}
