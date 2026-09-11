DROP TABLE IF EXISTS trees;
--SPLIT
CREATE TABLE IF NOT EXISTS trees (
    id INTEGER PRIMARY KEY,
    x REAL NOT NULL,
    y REAL NOT NULL,
    ownership TEXT NOT NULL,
    genus TEXT NOT NULL,
    food TEXT NOT NULL,
    common_name TEXT NOT NULL,
    last_modified DATETIME,
    service_status TEXT
);
--SPLIT
CREATE TABLE regions (
    north REAL NOT NULL,
    east REAL NOT NULL,
    south REAL NOT NULL,
    west REAL NOT NULL,
    last_updated DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    type TEXT NOT NULL,
    PRIMARY KEY (type, north, east, south, west)
);
--SPLIT
DROP TABLE IF EXISTS from_api;
--SPLIT
CREATE TABLE IF NOT EXISTS from_api (
    tree_id INTEGER PRIMARY KEY,
    date_retrieved DATETIME NOT NULL,
    FOREIGN KEY (tree_id) REFERENCES trees(id)
);
--SPLIT
DROP TABLE IF EXISTS from_user;
--SPLIT
CREATE TABLE IF NOT EXISTS from_user (
    tree_id INTEGER PRIMARY KEY,
    when_created DATETIME NOT NULL,
    FOREIGN KEY (tree_id) REFERENCES trees(id)
);
--SPLIT
DROP TABLE IF EXISTS ingredient;
--SPLIT
CREATE TABLE IF NOT EXISTS ingredient (
    ingredient_id int NOT NULL,
    serving_id int NOT NULL,
    ingredient_name TEXT NOT NULL,
    ingredient_unit_number int NOT NULL,
    ingredient_measurement_description TEXT NOT NULL,
    ingredient_description NOT NULL,
    PRIMARY KEY (serving_id, ingredient_id)
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
     retrieval_time DATETIME NOT NULL,
     recipe_image TEXT NOT NULL,
     cooking_time_min int NOT NULL,
     preparation_time_min int NOT NULL
);
--SPLIT
DROP TABLE IF EXISTS ingredients_in_recipe;
--SPLIT
CREATE TABLE IF NOT EXISTS ingredients_in_recipe (
    recipe_id INTEGER NOT NULL,
    ingredient_id INTEGER NOT NULL,
    serving_id INTEGER NOT NULL,
    ingredient_description TEXT NOT NULL,
    PRIMARY KEY (recipe_id, ingredient_id, serving_id),
    FOREIGN KEY (recipe_id) REFERENCES recipes(recipe_id),
    FOREIGN KEY (ingredient_id) REFERENCES ingredient(ingredient_id),
    FOREIGN KEY (serving_id) REFERENCES ingredient(serving_id)
);
--SPLIT
DROP TABLE IF EXISTS note;
--SPLIT
CREATE TABLE IF NOT EXISTS note (
    id INTEGER PRIMARY KEY,
    time_created DATETIME NOT NULL,
    name TEXT NOT NULL,
    tree_id INTEGER,
    recipe_id INTEGER,
    FOREIGN KEY (tree_id) REFERENCES trees(id),
    FOREIGN KEY (recipe_id) REFERENCES recipes(id)
);
--SPLIT
DROP TABLE IF EXISTS food;
--SPLIT
CREATE TABLE IF NOT EXISTS food (
    common_name TEXT PRIMARY KEY,
    food_type TEXT NOT NULL
);
--SPLIT
DROP TABLE IF EXISTS recipe_uses_food;
--SPLIT
CREATE TABLE IF NOT EXISTS recipe_uses_food (
    common_name TEXT,
    recipe_id INTEGER,
    PRIMARY KEY (common_name, recipe_id),
    FOREIGN KEY (common_name) REFERENCES food(common_name),
    FOREIGN KEY (recipe_id) REFERENCES recipes(recipe_id)
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
    FOREIGN KEY (tree_id) REFERENCES trees(id),
    food TEXT NOT NULL,
    common_name TEXT NOT NULL
);
--SPLIT
DROP PROCEDURE IF EXISTS get_recipe_info;
--SPLIT
CREATE PROCEDURE get_recipe_info
    @id INTEGER = NULL
IF id = NULL THEN
BEGIN
SELECT recipe.recipe_id, recipe.recipe_name, GROUP_CONCAT(ingredients_in_recipe.ingredient_description) AS recipe_ingredients, recipe.recipe_method, recipe.carbohydrates ,recipe.calories, recipe.carbohydrates, recipe.protein, recipe.fat, recipe.recipe_description, recipe.retrieval_time
FROM recipes
         JOIN ingredients_in_recipe
              ON recipe.recipe_id = ingredients_in_recipe.recipe_id
END;
ELSE
BEGIN
SELECT recipe.recipe_id, recipe.recipe_name, GROUP_CONCAT(ingredients_in_recipe.ingredient_description) AS recipe_ingredients, recipe.recipe_method, recipe.calories, recipe.carbohydrates, recipe.protein, recipe.fat, recipe.recipe_description, recipe.retrieval_time
FROM recipes
         JOIN ingredients_in_recipe
              ON recipe.recipe_id = ingredients_in_recipe.recipe_id
WHERE recipe.recipe_id = @id;
END;
