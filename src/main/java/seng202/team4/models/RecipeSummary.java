package seng202.team4.models;

public class RecipeSummary {
    private final int recipeId;
    private final String recipeName;
    private final String recipeImageUrl;


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
