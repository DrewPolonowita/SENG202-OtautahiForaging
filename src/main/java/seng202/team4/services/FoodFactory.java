package seng202.team4.services;

import seng202.team4.models.Food;
import seng202.team4.models.FoodServing;
import seng202.team4.models.api.Food.FoodByID.Serving;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that acts as the place to create {@link Food} and {@link FoodServing} objects around the application
 */
public final class FoodFactory {
    private FoodFactory() {
    }

    /**
     * Create {@link Food} objects
     * @param id unique food id
     * @param servings list of food serving objects
     * @return {@link Food} object
     */
    public static Food create(
            int id,
            String name,
            String type,
            List<FoodServing> servings
    ) {
        return new Food(id, name, type, servings);
    }

    /**
     * Create a {@link FoodServing} object that is part of the food object
     * @param servingId unique id
     * @return {@link FoodServing}
     */
    public static FoodServing createServing(
            int servingId,
            String description,
            double calories,
            double carbohydrates,
            double protein,
            double fat
    ) {
        return new FoodServing(
                servingId,
                description,
                calories,
                carbohydrates,
                protein,
                fat
        );
    }

    /**
     * Create the full list of {@link FoodServing} for use
     * @param apiServings {@link List<Serving>} list of json record objects
     * @return {@link List<FoodServing>} to go in food object
     */
    public static List<FoodServing> createServingsFromApi(List<Serving> apiServings) {
        List<FoodServing> servings = new ArrayList<>();

        for (Serving serving : apiServings) {
            servings.add(createServing(
                    serving.servingId(),
                    serving.servingDescription(),
                    serving.calories(),
                    serving.carbohydrate(),
                    serving.protein(),
                    serving.fat()
            ));
        }

        return servings;
    }
}
