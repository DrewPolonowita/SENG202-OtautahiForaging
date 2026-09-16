package seng202.team4.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team4.models.Food;
import seng202.team4.models.api.Food.FoodByID.FoodByIdRecord;
import seng202.team4.models.api.Food.FoodByID.FoodByIdResponse;
import seng202.team4.models.api.Food.SearchFood.FoodSearchResponse;
import seng202.team4.models.api.Request;
import seng202.team4.services.api.FoodClientInterface;



public class FoodApiHandler {
    private static final Logger LOG = LogManager.getLogger(FoodApiHandler.class);
    private final FoodClientInterface foodApiClient;

    /**
     * Constructor for the RecipeApiHandler
     */
    public FoodApiHandler(FoodClientInterface foodApiClient) {
        this.foodApiClient = foodApiClient;
    }

    public int getFoodBySearchFromApi(String searchExpression) {
        Request<FoodSearchResponse> response = foodApiClient.searchFood(searchExpression, 1);
        int responseCode = response.getResponseCode();
        if (responseCode != 200) {
            LOG.error("Error: getFoodBySearchFromApi doesn't return response, code: " + responseCode);
            return 0;
        }

        return response.getApiResponse().foods().food().get(0).foodId();
    }

    public Food getFoodById(int id, String foodName, String foodType) {
        Request<FoodByIdResponse> response = foodApiClient.getFoodById(id);
        int responseCode = response.getResponseCode();
        if (responseCode != 200) {
            LOG.error("Error: getFoodById doesn't return response, code: " + responseCode);
            return null;
        }

        Food createdFood = createFoodObjectById(response.getApiResponse().food(), foodName, foodType);
        return createdFood;
    }


    /**
     * Create a food model using a FoodByIdRecord
     * @param foodRecord a foodByIdRecord
     * @return a food model
     */
    public Food createFoodObjectById(FoodByIdRecord foodRecord, String foodName, String foodType) {
        int foodId = foodRecord.foodId();
        String name = foodName;
        String type = foodType;
        /*for (Map.Entry<String, List<String>> typeEntry : foodService.getTypeMap().entrySet()) {
            List<String> commonNames = typeEntry.getValue();
            for (String commonName : commonNames) {
                if (name.equals(commonName)) {
                    type = typeEntry.getKey();
                    break;
                }
            }
        }*/
        double calories = foodRecord.servings().serving().getFirst().calories();
        double fat = foodRecord.servings().serving().getFirst().fat();
        double protein = foodRecord.servings().serving().getFirst().protein();
        double carbohydrate = foodRecord.servings().serving().getFirst().carbohydrate();
        String servingDescription = foodRecord.servings().serving().getFirst().servingDescription();
        int servingId = foodRecord.servings().serving().getFirst().servingId();

        return new Food(
                foodId,
                name,
                type,
                calories,
                carbohydrate,
                protein,
                fat,
                servingDescription,
                servingId
        );
    }


}
