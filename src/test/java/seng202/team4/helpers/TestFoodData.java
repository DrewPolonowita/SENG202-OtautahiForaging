package seng202.team4.helpers;

import seng202.team4.models.Food;
import seng202.team4.models.api.Food.FoodByID.FoodByIdRecord;
import seng202.team4.models.api.Food.FoodByID.FoodByIdResponse;
import seng202.team4.models.api.Food.FoodByID.FoodServings;
import seng202.team4.models.api.Food.FoodByID.Serving;


import java.util.List;

public class TestFoodData {

    public static Food createAppleFood() {
        return createFood(123, "Apple", "Fruit");
    }

    public static Food createFood(int id, String name, String type) {
        return new Food(
                id,
                name,
                type,
                100,
                20,
                5,
                2,
                "100 g",
                1234
        );
    }

    public static FoodByIdRecord createFakeFoodByIdRecord() {
        Serving serving = new Serving(
                1234,
                "100 g",
                100,
                20,
                5,
                2
        );

        FoodServings servings = new FoodServings(List.of(serving));

        return new FoodByIdRecord(
                123,
                "Apple",
                servings
        );
    }


}