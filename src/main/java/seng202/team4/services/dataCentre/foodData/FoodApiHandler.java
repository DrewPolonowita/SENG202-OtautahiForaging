package seng202.team4.services.dataCentre.foodData;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team4.models.Food;
import seng202.team4.models.api.Food.FoodByID.FoodByIdRecord;
import seng202.team4.models.api.Food.FoodByID.FoodByIdResponse;
import seng202.team4.models.api.Food.SearchFood.FoodSearchResponse;
import seng202.team4.models.api.Request;
import seng202.team4.services.api.FoodClientInterface;


/**
 * This handles the calls to the API package
 * @author Team 4
 */
public class FoodApiHandler {
    private static final Logger LOG = LogManager.getLogger(FoodApiHandler.class);
    private final FoodClientInterface foodApiClient;

    /**
     * Constructor for the FoodApiHandler
     */
    public FoodApiHandler(FoodClientInterface foodApiClient) {
        this.foodApiClient = foodApiClient;
    }

    /**
     * Using a specific search expression, call the search food function on FatSecret api
     * Uses max result of 1, so we use the first option that comes back.
     * @param searchExpression A specific search expression, should be a food name e.g "Apple" or "Strawberry"
     * @return id of the food returned
     */
    public int getFoodBySearchFromApi(String searchExpression) {
        Request<FoodSearchResponse> response = foodApiClient.searchFood(searchExpression, 1);
        int responseCode = response.getResponseCode();
        if (responseCode != 200) {
            LOG.error("Error: getFoodBySearchFromApi doesn't return response, code: {}", responseCode);
            return 0;
        }

        return response.getApiResponse().foods().food().getFirst().foodId();
    }

    /**
     * Call the FatSecret get food by id function
     * Input specific foodName for use as the food name in the object because sometimes
     * "apple" is "apples" or "strawberry" is "strawberries" in the fatsecret api
     * @param id food id (that we can store permanently)
     * @param foodName specific name such as "Apple" or "Cherry" that we define
     * @param foodType type that we define
     * @return Food model using the fatsecret api details (null if it fails)
     */
    public Food getFoodById(int id, String foodName, String foodType) {
        Request<FoodByIdResponse> response = foodApiClient.getFoodById(id);
        int responseCode = response.getResponseCode();
        if (responseCode != 200) {
            LOG.error("Error: getFoodById doesn't return response, code: {}", responseCode);
            return null;
        }

        return createFoodObjectById(response.getApiResponse().food(), foodName, foodType);
    }


    /**
     * Create a food model using a FoodByIdRecord
     * @param foodRecord a foodByIdRecord
     * @param foodName the name we define as the app, e.g "Apple" or "Cherry"
     * @param foodType the type defined by the type map e.g "Fruit" or "Nut"
     * @return a food model
     */
    public Food createFoodObjectById(FoodByIdRecord foodRecord, String foodName, String foodType) {
        int foodId = foodRecord.foodId();

        double calories = foodRecord.servings().serving().getFirst().calories();
        double fat = foodRecord.servings().serving().getFirst().fat();
        double protein = foodRecord.servings().serving().getFirst().protein();
        double carbohydrate = foodRecord.servings().serving().getFirst().carbohydrate();
        String servingDescription = foodRecord.servings().serving().getFirst().servingDescription();
        int servingId = foodRecord.servings().serving().getFirst().servingId();

        return new Food(
                foodId,
                foodName,
                foodType,
                calories,
                carbohydrate,
                protein,
                fat,
                servingDescription,
                servingId
        );
    }


}
