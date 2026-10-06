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
                        + "<button class=\"recipe-btn\" onclick=\"javaScriptBridge.goToRecipes('%s')\">Go to recipes</button>"
                        + "</div></div>",
                tree.getFood(),
                tree.getGenus(),
                buildNutritionSection(),
                tree.getId(),
                tree.getFood()
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
        FoodServing baseline = food.getServings().stream()
                .filter(serving -> "100 g".equalsIgnoreCase(serving.getDescription()))
                .findFirst()
                .orElse(null);

        if (baseline == null) {
            return "a";
        }

        return String.format(
                "<label \">Enter grams:</label>"
                        + "<input class=\"serving-select\" type=\"text\" value=\"100\" "
                        + "data-calories=\"%f\" data-carbohydrates=\"%f\" "
                        + "data-protein=\"%f\" data-fat=\"%f\" "
                        + "oninput=\"this.value = this.value.replace(/[^0-9]/g, ''); "
                        + "updateNutrition(this)\">"
                        + "<div class=\"nutrition-info\">"
                        + "<div class=\"calories\">Calories: %f kcal</div>"
                        + "<div class=\"carbohydrates\">Carbohydrates: %f g</div>"
                        + "<div class=\"protein\">Protein: %f g</div>"
                        + "<div class=\"fat\">Fat: %f g</div>"
                        + "</div>",
                baseline.getCalories(),
                baseline.getCarbohydrates(),
                baseline.getProtein(),
                baseline.getFat(),
                baseline.getCalories(),
                baseline.getCarbohydrates(),
                baseline.getProtein(),
                baseline.getFat()
        );
    }
}
