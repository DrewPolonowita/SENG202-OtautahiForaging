package seng202.team4.unittests.services;

import org.junit.jupiter.api.Test;
import seng202.team4.models.Food;
import seng202.team4.models.FoodServing;
import seng202.team4.models.TreeModel;
import seng202.team4.services.TreePopupBuilder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TreePopupBuilderTest {

    /**
     * Checks that the popup displays the right values without opening JavaFX
     */
    @Test
    void popupDisplaysNutrition() {
        TreeModel tree = mock(TreeModel.class);
        when(tree.getId()).thenReturn(123);
        when(tree.getFood()).thenReturn("Apple");

        FoodServing serving = mock(FoodServing.class);
        when(serving.getDescription()).thenReturn("100 g");
        when(serving.getCalories()).thenReturn(52.0);
        when(serving.getCarbohydrates()).thenReturn(14.0);
        when(serving.getProtein()).thenReturn(0.3);
        when(serving.getFat()).thenReturn(0.2);

        Food food = mock(Food.class);
        when(food.getServings()).thenReturn(List.of(serving));

        String html = new TreePopupBuilder.Builder(tree).food(food).build();

        assertTrue(html.contains("Nutrition per 100 g"));
        assertTrue(html.contains("<span>Calories</span><span>52.0 kcal</span>"));
        assertTrue(html.contains("<span>Carbs</span><span>14.0 g</span>"));
        assertTrue(html.contains("<span>Protein</span><span>0.3 g</span>"));
        assertTrue(html.contains("<span>Fat</span><span>0.2 g</span>"));
    }

    /**
     * Checks that the popup shows Nutrition unavailable if unavailable
     */
    @Test
    void popupDisplaysMessageIfServingMissing() {
        TreeModel tree = mock(TreeModel.class);
        when(tree.getFood()).thenReturn("Apple");
        Food food = mock(Food.class);
        when(food.getServings()).thenReturn(List.of());

        String html = new TreePopupBuilder.Builder(tree)
                .food(food)
                .message("Nutrition unavailable")
                .build();

        assertTrue(html.contains("Nutrition unavailable"));
    }

    /**
     * Checks that the popup shows the nutrition for the right food type
     */
    @Test
    void popupButtonsUseCorrectAttributes() {
        TreeModel tree = mock(TreeModel.class);
        when(tree.getId()).thenReturn(456);
        when(tree.getFood()).thenReturn("Pear");
        String html = new TreePopupBuilder.Builder(tree).build();

        assertTrue(html.contains("javaScriptBridge.addTreeToJournal(456)"));
        assertTrue(html.contains("javaScriptBridge.goToRecipes('Pear')"));
    }
}
