package seng202.team4.services.dataCentre.recipeData;

import seng202.team4.models.Recipe;

import java.util.List;

public interface RecipeDataManager {

    List<Recipe> getRecipesForFood(String food);
}
