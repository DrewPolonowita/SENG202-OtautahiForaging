package seng202.team4.services;

import org.jetbrains.annotations.Nullable;
import seng202.team4.models.Recipe;
import seng202.team4.models.RecipeSummary;
import seng202.team4.models.api.Recipe.RecipeByID.RecipeByIdRecord;
import seng202.team4.models.api.Recipe.RecipeByID.RecipeDirection;
import seng202.team4.models.api.Recipe.RecipeByID.RecipeIngredient;
import seng202.team4.models.api.Recipe.RecipeNutrition;
import seng202.team4.models.api.Recipe.SearchRecipe.RecipeSearchRecord;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Class that acts as a place for making recipe objects around the application
 */
public final class RecipeFactory {
    private RecipeFactory() {
    }

    /**
     * Create a {@link Recipe} object
     * @param id unique id
     * @param retrievalTime time the object was retrieved from the API
     * @param image url
     * @return recipe object
     */
    public static Recipe create(
            int id,
            String name,
            String method,
            List<String> ingredients,
            double carbohydrates,
            double calories,
            double protein,
            double fat,
            String description,
            LocalDateTime retrievalTime,
            String image,
            int cookingTime,
            int preparationTime
    ) {
        return new Recipe(
                id,
                name,
                method,
                ingredients,
                carbohydrates,
                calories,
                protein,
                fat,
                description,
                retrievalTime,
                image,
                cookingTime,
                preparationTime
        );
    }

    /**
     * Creates a recipe using a RecipeByIdRecord
     * @param recipeRecord a RecipeByIdRecord
     * @return a Recipe model
     */
    public static Recipe createRecipeObjectById(RecipeByIdRecord recipeRecord) {

        int recipeId = recipeRecord.recipeId();
        String recipeName = recipeRecord.recipeName();
        String method = buildMethodString(recipeRecord.directions()
                == null ? List.of() : recipeRecord.directions().direction());
        List<String> ingredients = getIngredientString(recipeRecord.ingredients()
                == null ? List.of() : recipeRecord.ingredients().ingredient());
        RecipeNutrition nutrition = getRecipeNutrition(recipeRecord);

        String description = recipeRecord.recipeDescription();

        String imageUrl = getUrl(recipeRecord);
        int cookingTime = recipeRecord.cookingTime();
        int preparationTime = recipeRecord.preparationTime();


        return create(recipeId,
                recipeName,
                method,
                ingredients,
                nutrition == null ? 0 : nutrition.carbohydrate(),
                nutrition == null ? 0 : nutrition.calories(),
                nutrition == null ? 0 : nutrition.protein(),
                nutrition == null ? 0 : nutrition.fat(),
                description,
                LocalDateTime.now(),
                imageUrl,
                cookingTime,
                preparationTime
        );
    }

    public static RecipeSummary createRecipeSummaryFromSearch(RecipeSearchRecord record) {
        return new RecipeSummary(
                record.id(),
                record.recipeName(),
                record.recipeImageUrl()
        );
    }

    /**
     * Get the nutrition object for a recipe
     * @param recipeRecord Recipe record to search
     * @return RecipeNutrition record
     */
    private static @Nullable RecipeNutrition getRecipeNutrition(RecipeByIdRecord recipeRecord) {
        RecipeNutrition nutrition = null;
        if (recipeRecord.servingSizes() != null) {
            nutrition = recipeRecord.servingSizes().serving();
        }
        return nutrition;
    }

    /**
     * Get image url from the recipe record
     * @param recipeRecord recipe record to search
     * @return String url
     */
    private static @Nullable String getUrl(RecipeByIdRecord recipeRecord) {
        String imageUrl = null;
        if (recipeRecord.images()
                != null && recipeRecord.images().imageUrl()
                != null && !recipeRecord.images().imageUrl().isEmpty()
        ) {
            imageUrl = recipeRecord.images().imageUrl().getFirst();
        }
        return imageUrl;
    }

    /**
     * Take RecipeIngredient objects and get all their ingredient descriptions
     * @param ingredients list of ingredients
     * @return a list of all the ingredient descriptions
     */
    public static List<String> getIngredientString(List<RecipeIngredient> ingredients) {
        List<String> ingredientStrings = new ArrayList<>();
        for (RecipeIngredient ingredient : ingredients) {
            ingredientStrings.add(ingredient.ingredientDescription());
        }
        return ingredientStrings;
    }


    /**
     * Method to build string of recipe method with steps each on a new line
     * @param directions a list of RecipeDirection models
     * @return a string containing the recipe's method
     */
    private static String buildMethodString(List<RecipeDirection> directions) {
        List<RecipeDirection> sortedDirections = directions
                .stream()
                .sorted(Comparator.comparing(RecipeDirection::directionNumber))
                .toList();

        StringBuilder stringBuilder = new StringBuilder();
        for (RecipeDirection sortedDirection : sortedDirections) {
            stringBuilder.append(sortedDirection.directionNumber());
            stringBuilder.append(": ").append(sortedDirection.directionDescription()).append("\n");
        }
        return stringBuilder.toString();
    }
}
