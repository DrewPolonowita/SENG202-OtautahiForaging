package seng202.team4.services;

import seng202.team4.models.Food;
import seng202.team4.models.FoodServing;
import seng202.team4.models.TreeModel;

/**
 * Class with methods to build the tree popup html using the builder pattern
 */
public class TreePopupBuilder {
    private final TreeModel tree;
    private final Food food;
    private final String message;

    /**
     * Constructor
     */
    TreePopupBuilder(Builder builder) {
        this.tree = builder.tree;
        this.food = builder.food;
        this.message = builder.message;
    }

    /**
     * Builder pattern builder class
     */
    public static class Builder {
        private final TreeModel tree;
        private Food food;
        private String message;

        public Builder(TreeModel tree) {
            this.tree = tree;
        }

        /**
         * @param food food the marker is about
         * @return food info
         */
        public Builder food(Food food) {
            this.food = food;
            return this;
        }

        /**
         * @param message message to display to user
         * @return message to use in html building message
         */
        public Builder message(String message) {
            this.message = message;
            return this;
        }

        /**
         * return the popup with either food or message
         * @return String html to pass to javascript
         */
        public String build() {
            return new TreePopupBuilder(this).buildHtml();
        }
    }

    /**
     * Gives the HTML for our marker popup
     * @return
     */
    private String buildHtml() {
        String popup = String.format(
                "<div class=\"tree-card\">"
                        + "<div class=\"tree-card-body\">"
                        + "<b>%s</b><br>"
                        + "<span style=\"font-size: 0.8em; opacity: 0.7;\">Genus: %s</span>"
                        + "%s"
                        + "<button class=\"journal-btn\" onclick=\"javaScriptBridge.addTreeToJournal(%d)\">Add to Journal</button>"
                        + "</div></div>",
                tree.getFood(),
                tree.getGenus(),
                buildNutritionSection(),
                tree.getId()
        );
        return popup;
    }

    /**
     * Either uses the food information to build a nutrition section, or just displays the status message
     * @return nutrition section or status message
     */
    private String buildNutritionSection() {
        if (food != null) {
            return buildNutritionHtml();
        }

        return String.format(
                "<div class=\"nutrition-info\">%s</div>",
                message == null ? "" : message
        );
    }

    private String buildNutritionHtml() {
        FoodServing defaultServing = food.getDefaultServing();

        return String.format(
                "<select class=\"serving-select\" onchange=\"updateNutrition(this)\">"
                        + "%s"
                        + "</select>"
                        + "<div class=\"nutrition-info\">"
                        + "<div class=\"calories\">Calories: %f kcal</div>"
                        + "<div class=\"carbohydrates\">Carbohydrates: %f g</div>"
                        + "<div class=\"protein\">Protein: %f g</div>"
                        + "<div class=\"fat\">Fat: %f g</div>"
                        + "</div>",
                buildServingOptions(),
                defaultServing.getCalories(),
                defaultServing.getCarbohydrates(),
                defaultServing.getProtein(),
                defaultServing.getFat()
        );
    }

    private String buildServingOptions() {
        StringBuilder servingOptions = new StringBuilder();

        for (FoodServing serving : food.getServings()) {
            servingOptions.append(String.format(
                    "<option data-calories=\"%f\" data-carbohydrates=\"%f\" data-protein=\"%f\" data-fat=\"%f\">%s</option>",
                    serving.getCalories(),
                    serving.getCarbohydrates(),
                    serving.getProtein(),
                    serving.getFat(),
                    serving.getDescription()
            ));
        }

        return servingOptions.toString();
    }
}
