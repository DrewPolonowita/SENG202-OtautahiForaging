package seng202.team4.helpers;


import seng202.team4.models.Recipe;
import seng202.team4.models.api.Recipe.RecipeByID.*;
import seng202.team4.models.api.Recipe.RecipeNutrition;


import java.util.ArrayList;
import java.util.List;


public class TestRecipeData {

    public static RecipeByIdRecord createFakeRecipeByIdRecord() {
        RecipeNutrition nutrition = new RecipeNutrition(123, 345,567,789);
        RecipeServingSizes servingSizes = new RecipeServingSizes(nutrition);
        RecipeDirections directions = new RecipeDirections(
                List.of(new RecipeDirection(1, "Direction step 1"),
                        new RecipeDirection(2, "Direction step 2"))
        );
        RecipeIngredients ingredients = new RecipeIngredients(
                List.of(new RecipeIngredient(123, "Apple", 1234, 1, "", "1 Apple"))
        );
        RecipeImages images = new RecipeImages(
                List.of("Image URL")
        );
        return new RecipeByIdRecord(
                123,
                "Recipe Name",
                "Description",
                servingSizes,
                directions,
                ingredients,
                10,
                10,
                images
        );
    }

    public static List<RecipeIngredient> createFakeRecipeIngredient() {
        List<RecipeIngredient> testIngredients = new ArrayList<>();
        testIngredients.add(new RecipeIngredient(123, "TestIngredient", 1234, 100, "g", "100 g TestIngredient"));
        testIngredients.add(new RecipeIngredient(234, "TestIngredient2", 4567, 200, "cups", "200 cups TestIngredient2"));
        return testIngredients;
    }

    public static List<Recipe> createRecipeModels() {
        List<Recipe> testRecipes = new ArrayList<>();
        List<String> testIngredients1 = List.of("1 Apple", "100 g butter", "2 Peaches");
        List<String> testIngredients2 = List.of("2 Apple", "1 pinch cinnamon", "1 teaspoon honey");
        List<String> testIngredients3 = List.of("ingredient 1", "ingredient 2", "ingredient 3");
        testRecipes.add(new Recipe(1234, "Test1", "Methods 1: tester", testIngredients1, 123, 456, 187, 168, "Description 1", null,  "https://m.ftscrt.com/static/recipe/bf0c5912-9cf8-4e7a-b07a-6703c4b77082.jpg", 10, 10));
        testRecipes.add(new Recipe(2345, "Test2", "Methods 2: tester", testIngredients2, 126, 123, 3, 22, "Description 2", null, "https://m.ftscrt.com/static/recipe/bf0c5912-9cf8-4e7a-b07a-6703c4b77082.jpg", 20, 20));
        testRecipes.add(new Recipe(1235, "Test3", "Methods 3: tester", testIngredients3, 125, 421, 432, 431, "Description 3", null, "https://m.ftscrt.com/static/recipe/bf0c5912-9cf8-4e7a-b07a-6703c4b77082.jpg", 30, 30));
        return testRecipes;
    }

    public static Recipe createUpdatedRecipe(Recipe recipe) {
        return new Recipe(
                recipe.getId(),
                "new name",
                "new method",
                recipe.getIngredients(),
                900,
                800,
                700,
                600,
                "new description",
                null,
                "new image url",
                500,
                400
        );
    }

    public static Recipe createNewRecipe() {
        return new Recipe(
                41239841,
                "new name",
                "new method",
                List.of("Test ingredient"),
                900,
                800,
                700,
                600,
                "new description",
                null,
                "new image url",
                500,
                400
        );
    }



}
