package seng202.team4.repository;

import seng202.team4.models.RecipeSummary;

import java.util.List;

public interface RecipeSummaryRepository extends DAOInterface<RecipeSummary, Integer> {

    List<RecipeSummary> getRecipeSummariesByFoodName(String food);

    void addRecipeUseFood(String food, int recipeId);
}
