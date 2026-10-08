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

        /**
         * Constructor
         * @param tree tree with popup being built
         */
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
     * @return returns
     */
    private String buildHtml() {
        String popup = String.format(
                "<div class=\"tree-card\">"
                        + "<div class=\"tree-card-body\">"
                        + "<b>%s</b>"
                        + "%s"
                        + "<button class=\"journal-btn\" onclick=\"javaScriptBridge.addTreeToJournal(%d)\">Add to Journal</button>"
                        + "<button class=\"recipe-btn\" onclick=\"javaScriptBridge.goToRecipes('%s')\">Go to recipes</button>"
                        + "</div></div>",
                tree.getFood(),
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
            return String.format(
                    "<div class=\"nutrition-info\">%s>/div>",
                    message == null ? "" : message
            );
        }

        return String.format(
                "<div class=\"nutrition-heading\">Nutrition per 100 g</div>"
                        + "<div class=\"nutrition-info\">"
                        + "<div><span>Calories</span><span>%.1f kcal</span></div>"
                        + "<div><span>Carbs</span><span>%.1f g</span></div>"
                        + "<div><span>Protein</span><span>%.1f g</span></div>"
                        + "<div><span>Fat</span><span>%.1f g</span></div>"
                        + "</div>",
                baseline.getCalories(),
                baseline.getCarbohydrates(),
                baseline.getProtein(),
                baseline.getFat()
        );
    }
}
