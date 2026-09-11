package seng202.team4.helpers;


import seng202.team4.models.Recipe;
import seng202.team4.models.api.Recipe.RecipeByID.RecipeByIdRecord;
import seng202.team4.models.api.Recipe.RecipeByID.RecipeDirection;
import seng202.team4.models.api.Recipe.RecipeByID.RecipeIngredient;
import seng202.team4.models.api.Recipe.RecipeByID.RecipeServingSizes;
import seng202.team4.models.api.Recipe.RecipeNutrition;


import java.util.ArrayList;
import java.util.List;


public class TestRecipeData {


    public static RecipeByIdRecord createFakeRecipeByIdRecord() {
        RecipeNutrition nutrition = new RecipeNutrition(123, 345,567,789);
        RecipeServingSizes servingSizes = new RecipeServingSizes(nutrition);
        List<RecipeDirection> directions = List.of(
                new RecipeDirection(1, "Direction step 1"),
                new RecipeDirection(2, "Direction step 2"));
        List<RecipeIngredient> ingredients = List.of(new RecipeIngredient(123, "TestIngredient", 1234, 100, "g", "Description"));
        return new RecipeByIdRecord(
                123,
                "Recipe Name",
                "Description",
                servingSizes,
                directions,
                ingredients,
                10,
                10,
                "Image URL"
        );


    }


    public static List<RecipeIngredient> createFakeRecipeIngredient() {
        List<RecipeIngredient> testIngredients = new ArrayList<>();
        testIngredients.add(new RecipeIngredient(123,"TestIngredient", 1234, 100, "g", "Description"));
        testIngredients.add(new RecipeIngredient(234, "TestIngredient2", 4567, 200,"cups", "Description"));
        return testIngredients;
    }

    public static List<Recipe> createRecipeModels() {
        List<Recipe> testRecipes = new ArrayList<>();
        List<String> testIngredients1 = List.of("ingredient 1", "ingredient 2", "ingredient 3");
        List<String> testIngredients2 = List.of("ingredient 1", "ingredient 2", "ingredient 3");
        List<String> testIngredients3 = List.of("ingredient 1", "ingredient 2", "ingredient 3");
        testRecipes.add(new Recipe(1234, "Test1", "Methods 1: tester", testIngredients1, 123, 456, 187, 168, "Description", null,  "https://m.ftscrt.com/static/recipe/bf0c5912-9cf8-4e7a-b07a-6703c4b77082.jpg", 10, 10));
        testRecipes.add(new Recipe(2345, "Test1", "Methods 2: tester", testIngredients2, 123, 456, 187, 168, "Description", null, "https://m.ftscrt.com/static/recipe/bf0c5912-9cf8-4e7a-b07a-6703c4b77082.jpg", 20, 20));
        testRecipes.add(new Recipe(1234, "Test1", "Methods 3: tester", testIngredients3, 123, 456, 187, 168, "Description", null, "https://m.ftscrt.com/static/recipe/bf0c5912-9cf8-4e7a-b07a-6703c4b77082.jpg", 30, 30));
        return testRecipes;
    }
}
