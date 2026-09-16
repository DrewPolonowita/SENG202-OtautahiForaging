package seng202.team4.services.dataCentre.foodData;

import seng202.team4.models.Food;
import seng202.team4.models.api.Food.FoodByID.FoodByIdRecord;

import java.util.List;

public interface FoodDataManager {

    List<Food> getFoods();

}
