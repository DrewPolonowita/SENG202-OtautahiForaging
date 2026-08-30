DROP TABLE IF EXISTS trees;
--SPLIT
CREATE TABLE IF NOT EXISTS trees (
    id INTEGER PRIMARY KEY,
    x REAL NOT NULL,
    y REAL NOT NULL,
    ownership TEXT NOT NULL,
    genus TEXT NOT NULL,
    food TEXT NOT NULL, -- food_name?
    common_name TEXT NOT NULL,
    last_modified DATETIME NOT NULL,
    service_status TEXT NOT NULL
);
--SPLIT
-- Trees subclass table 1
DROP TABLE IF EXISTS from_api;
--SPLIT
CREATE TABLE IF NOT EXISTS from_api (
    tree_id INTEGER PRIMARY KEY,
    date_retrieved DATETIME NOT NULL,
    FOREIGN KEY (tree_id) REFERENCES tree(id)
);
-SPLIT
-- Trees subclass table 2
DROP TABLE IF EXISTS from_user;
--SPLIT
CREATE TABLE IF NOT EXISTS from_user (
    tree_id INTEGER PRIMARY KEY,
    when_created DATETIME NOT NULL,
    FOREIGN KEY (tree_id) REFERENCES tree(id)
);
--SPLIT
DROP TABLE IF EXISTS recipes;
--SPLIT
CREATE TABLE IF NOT EXISTS recipes (
    recipe_id INTEGER PRIMARY KEY,
    recipe_name TEXT NOT NULL,
    recipe_method TEXT NOT NULL,
    carbohydrates DOUBLE,
    calories DOUBLE,
    protein DOUBLE,
    fat DOUBLE,
    recipe_description TEXT NOT NULL,
    retrieval_time DATETIME NOT NULL
);
--SPLIT
DROP TABLE IF EXISTS ingredients;
--SPLIT
CREATE TABLE IF NOT EXISTS ingredients (
    recipe_id INTEGER NOT NULL,
    ingredient_name TEXT NOT NULL,
    PRIMARY KEY (recipe_id, ingredient_name),
    FOREIGN KEY (recipe_id) REFERENCES recipes(recipe_id)
    );
--SPLIT
DROP TABLE IF EXISTS note;
--SPLIT
CREATE TABLE IF NOT EXISTS note (
    id INTEGER PRIMARY KEY,
    time_created DATETIME NOT NULL,
    note_name TEXT NOT NULL,
    tree_id INTEGER,
    recipe_id INTEGER,
    FOREIGN KEY (tree_id) REFERENCES tree(id),
    FOREIGN KEY (recipe_id) REFERENCES recipe(id)
);
--SPLIT
DROP TABLE IF EXISTS food;
--SPLIT
CREATE TABLE IF NOT EXISTS food (
    common_name TEXT PRIMARY KEY,
    food_type TEXT NOT NULL,
    nutritional_information TEXT,
);
--SPLIT
DROP TABLE IF EXISTS region;
--SPLIT
CREATE TABLE IF NOT EXISTS region (
    x INTEGER NOT NULL,
    y INTEGER NOT NULL,
    retrieval_time DATETIME NOT NULL,
    PRIMARY KEY (y, x)
);
--SPLIT
DROP TABLE IF EXISTS recipe_uses_food;
--SPLIT
CREATE TABLE IF NOT EXISTS recipe_uses_food (
    common_name TEXT,
    recipe_id INTEGER,
    PRIMARY KEY (common_name, recipe.id),
    FOREIGN KEY (common_name) REFERENCES food(common_name),
    FOREIGN KEY (recipe_id) REFERENCES recipe(id);
);
--SPLIT
DROP TABLE IF EXISTS region_contains_tree;
--SPLIT
CREATE TABLE IF NOT EXISTS region_contains_tree (
    x INTEGER NOT NULL,
    y INTEGER NOT NULL,
    tree_id INTEGER NOT NULL,
    PRIMARY KEY (x, y, tree_id),
    FOREIGN KEY (x) REFERENCES region(x),
    FOREIGN KEY (y) REFERENCES region(y),
    FOREIGN KEY (tree_id) REFERENCES tree(id);
    food TEXT NOT NULL,
    common_name TEXT NOT NULL
)
--SPLIT
DROP PROCEDURE IF EXISTS get_recipe_info;
--SPLIT
CREATE PROCEDURE get_recipe_info
    @id INTEGER = NULL
IF id = NULL THEN
    BEGIN
        SELECT recipe.recipe_id, recipe.recipe_name, GROUP_CONCAT(ingredients.ingredient_name) AS recipeIngredients, recipe.recipe_method, recipe.carbohydrates ,recipe.calories, recipe.carbohydrates, recipe.protein, recipe.fat, recipe.recipe_description, recipe.retrieval_time
        FROM recipes
        JOIN ingredients
        ON recipe.recipe_id = ingredients.recipe_id
    END;
ELSE
    BEGIN
        SELECT recipe.recipe_id, recipe.recipe_name, GROUP_CONCAT(ingredients.ingredient_name) AS recipeIngredients, recipe.recipe_method, recipe.calories, recipe.carbohydrates, recipe.protein, recipe.fat, recipe.recipe_description, recipe.retrieval_time
        FROM recipes
        JOIN ingredients
        ON recipe.recipe_id = ingredients.recipe_id
        WHERE recipe.recipe_id = @id;
    END;
