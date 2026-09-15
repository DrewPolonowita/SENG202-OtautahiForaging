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
-- Trees subclass table 1
DROP TABLE IF EXISTS from_api;
--SPLIT
CREATE TABLE IF NOT EXISTS from_api (
    tree_id INTEGER PRIMARY KEY,
    date_retrieved DATETIME NOT NULL,
    FOREIGN KEY (tree_id) REFERENCES trees(id)
);
--SPLIT
-- Trees subclass table 2
DROP TABLE IF EXISTS from_user;
--SPLIT
CREATE TABLE IF NOT EXISTS from_user (
    tree_id INTEGER PRIMARY KEY,
    when_created DATETIME NOT NULL,
    FOREIGN KEY (tree_id) REFERENCES trees(id)
);
--SPLIT
DROP TABLE IF EXISTS recipe;
--SPLIT
CREATE TABLE IF NOT EXISTS recipe (
    id INTEGER PRIMARY KEY,
    name TEXT NOT NULL,
    ingredients TEXT NOT NULL,
    recipe_method TEXT NOT NULL,
    nutritional_information TEXT NOT NULL,
    description TEXT NOT NULL
);
--SPLIT
DROP TABLE IF EXISTS note;
--SPLIT
CREATE TABLE IF NOT EXISTS note (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    time_created DATETIME NOT NULL,
    name TEXT NOT NULL,
    description TEXT,
    image_path TEXT,
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
    nutritional_information NOT NULL,
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
CREATE TABLE IF NOT EXISTS uses (
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
*/
