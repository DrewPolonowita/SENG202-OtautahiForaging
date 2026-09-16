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
    ingredient_description TEXT NOT NULL,
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
    FOREIGN KEY (serving_id, ingredient_id) REFERENCES ingredient(serving_id, ingredient_id)
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
    FOREIGN KEY (recipe_id) REFERENCES recipes(recipe_id)
);
--SPLIT
DROP TABLE IF EXISTS food;
--SPLIT
CREATE TABLE IF NOT EXISTS food (
    food_id INTEGER PRIMARY KEY,
    common_name TEXT NOT NULL,
    food_type TEXT NOT NULL,
    calories DOUBLE,
    carbohydrates   DOUBLE,
    protein DOUBLE,
    fat DOUBLE,
    serving_description TEXT,
    serving_id INTEGER,
    retrieval_time DATETIME
);
--SPLIT
DROP TABLE IF EXISTS recipe_uses_food;
--SPLIT
CREATE TABLE IF NOT EXISTS recipe_uses_food (
    common_name TEXT,
    recipe_id INTEGER,
    PRIMARY KEY (common_name, recipe_id),
    FOREIGN KEY (recipe_id) REFERENCES recipes(recipe_id)
);

