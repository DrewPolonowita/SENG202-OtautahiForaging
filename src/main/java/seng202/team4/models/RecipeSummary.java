package seng202.team4.models;

/**
 * Represents a recipe summary
 * This is used as a less detailed recipe to display in ui for performance
 *
 * @author team 4
 */
public class RecipeSummary {
    private final int recipeId;
    private final String recipeName;
    private final String recipeImageUrl;


    /**
     * Constructor
     * @param recipeId id of the recipe
     * @param recipeName name of the recipe
     * @param recipeImageUrl url of the recipe
     */
    public RecipeSummary(int recipeId, String recipeName,  String recipeImageUrl) {
        this.recipeId = recipeId;
        this.recipeName = recipeName;
        this.recipeImageUrl = recipeImageUrl;
    }

    public int getRecipeId() {
        return recipeId;
    }

    public String getRecipeName() {
        return recipeName;
    }

    public String getRecipeImageUrl() {
        return recipeImageUrl;
    }
}
