package seng202.team4.services;

import seng202.team4.models.Food;
import seng202.team4.models.FoodServing;
import seng202.team4.models.api.Food.FoodByID.Serving;

import java.util.ArrayList;
import java.util.List;

public class FoodFactory {
    private FoodFactory() {
    }

    public static Food create(
            int id,
            String name,
            String type,
            List<FoodServing> servings
    ) {
        return new Food(id, name, type, servings);
    }

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
