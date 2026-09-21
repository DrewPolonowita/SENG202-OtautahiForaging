package seng202.team4.repository;


import org.apache.commons.lang3.NotImplementedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team4.models.Food;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


/**
 * DAO Interface implementation for food, handles food related actions to the database
 */
public class FoodDAO implements FoodRepository {
    private static final Logger LOG = LogManager.getLogger(FoodDAO.class);


    private final DatabaseManager databaseManager;


    /**
     * Constructor
     * @param databaseManager means of connecting to the database
     */
    public FoodDAO(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }


    /**
     * Method to return all food stored in the database
     * @return returns a list of Foods, empty list if failed
     */
    @Override
    public List<Food> getAll() {
        List<Food> food = new ArrayList<>();
        String sql = "SELECT * FROM food";
        try (Connection conn = databaseManager.connect();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)
        ) {
            while (rs.next()) {
                food.add(new Food(
                        rs.getInt("food_id"),
                        rs.getString("common_name"),
                        rs.getString("food_type"),
                        rs.getDouble("calories"),
                        rs.getDouble("carbohydrates"),
                        rs.getDouble("protein"),
                        rs.getDouble("fat"),
                        rs.getString("serving_description"),
                        rs.getInt("serving_id")
                ));
            }
            return food;
        } catch (SQLException sqlException) {
            LOG.error(sqlException);
            return new ArrayList<>();
        }
    }


    /**
     * Add a food object to the database
     * @param toAdd food object to add
     * @return unique food id of food if success, -1 if failed
     */
    @Override
    public int add(Food toAdd) {
        String sql = """
                   INSERT INTO food (
                   food_id,
                   common_name,
                   food_type,
                   calories,
                   carbohydrates,
                   protein,
                   fat,
                   serving_description,
                   serving_id,
                   retrieval_time
                   ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)
                   """;
        try (Connection conn = databaseManager.connect();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, toAdd.getId());
            ps.setString(2, toAdd.getName());
            ps.setString(3, toAdd.getType());
            ps.setDouble(4, toAdd.getCalories());
            ps.setDouble(5, toAdd.getCarbohydrates());
            ps.setDouble(6, toAdd.getProtein());
            ps.setDouble(7, toAdd.getFat());
            ps.setString(8, toAdd.getServingDescription());
            ps.setInt(9, toAdd.getServingId());


            ps.executeUpdate();
            return toAdd.getId();
        } catch (SQLException sqlException) {
            LOG.error(sqlException);
            return -1;
        }
    }


    /**
     * Deletes a specific food group from database
     * @param foodId of food group to delete
     */
    @Override
    public void delete (Integer foodId) {
        String sql = "DELETE FROM food WHERE food_id=?";
        try (Connection conn = databaseManager.connect();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, foodId);
            ps.executeUpdate();
        } catch (SQLException sqlException) {
            LOG.error(sqlException);
        }
    }


    /**
     * Method to get a list of food models of one shared type
     * @param type type of the object to get
     * @return food models of the specific type, empty list if failed
     */
    @Override
    public List<Food> getFoodByType(String type) {
        List<Food> foods = new ArrayList<>();
        String sql = "SELECT * FROM food WHERE food_type=?";
        try (Connection conn = databaseManager.connect();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, type);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    foods.add(new Food (
                            rs.getInt("food_id"),
                            rs.getString("common_name"),
                            rs.getString("food_type"),
                            rs.getDouble("calories"),
                            rs.getDouble("carbohydrates"),
                            rs.getDouble("protein"),
                            rs.getDouble("fat"),
                            rs.getString("serving_description"),
                            rs.getInt("serving_id")
                    ));
                }
            }
            return foods;
        } catch (SQLException sqlException) {
            LOG.error(sqlException);
            return new ArrayList<>();
        }
    }


    /**
     * Gets a food model matching the food grown on a particular tree
     * @param treeId the id of the specified tree
     * @return the food model corresponding to the food grown on the tree, or null if fails
     */
    @Override
    public Food getFoodbyTreeId(int treeId) {
        Food food = null;
        String sql = "SELECT * FROM food where common_name =(SELECT food FROM trees where id=?)";
        try (Connection conn = databaseManager.connect();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, treeId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    food = new Food (
                            rs.getInt("food_id"),
                            rs.getString("common_name"),
                            rs.getString("food_type"),
                            rs.getDouble("calories"),
                            rs.getDouble("carbohydrates"),
                            rs.getDouble("protein"),
                            rs.getDouble("fat"),
                            rs.getString("serving_description"),
                            rs.getInt("serving_id")
                    );
                }
            }
            return food;
        } catch (SQLException sqlException) {
            LOG.error(sqlException);
            return null;
        }
    }




    /**
     * Gets a single food model from the database by its common name
     * @param foodId the commonName of the food model being retrieved
     * @return food model with common Name given, or null if fails
     */
    @Override
    public Food getOne(Integer foodId) {
        Food food = null;
        String sql = "SELECT * FROM food WHERE food_id=?";
        try (Connection conn = databaseManager.connect();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setInt(1, foodId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    food = new Food (
                            rs.getInt("food_id"),
                            rs.getString("common_name"),
                            rs.getString("food_type"),
                            rs.getDouble("calories"),
                            rs.getDouble("carbohydrates"),
                            rs.getDouble("protein"),
                            rs.getDouble("fat"),
                            rs.getString("serving_description"),
                            rs.getInt("serving_id")
                    );
                }
            }
            return food;
        } catch (SQLException sqlException) {
            LOG.error(sqlException);
            return null;
        }
    }




    /**
     * Not implemented
     * @param toUpdate Object that needs to be updated
     * (this object must be able to identify itself and its previous self)
     */
    @Override
    public void update(Food toUpdate) {
        throw new NotImplementedException();
    }


    /**
     * Retrieves the food id for a given food with a specific common name
     * @param foodName the common name of a food
     * @return food id for the food with the specified common name if successful, otherwise null
     */
    @Override
    public Integer getFoodIdByFoodName(String foodName) {
        String sql = """
                SELECT food_id FROM food WHERE common_name=?
                """;

        try (Connection conn = databaseManager.connect();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, foodName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("food_id");
                }
            }
        } catch (SQLException sqlException) {
            LOG.error(sqlException);
        }
        return null;
    }

    /**
     * Method to check that the details stored for a specific food in the database are up to date as data can only be stored for up to 24 hours
     * @param foodId the id of the food being checked
     * @return true if up details are up-to-date and exist, false otherwise
     */
    @Override
    public boolean foodDetailsUpToDateAndExist(int foodId) {
        String sql = """
                SELECT 1
                FROM food
                WHERE food_id = ?
                AND retrieval_time >= DATETIME('now', '-24 hours')
                """;
        try (Connection conn = databaseManager.connect();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, foodId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            LOG.error(e);
            return false;
        }
    }

    /**
     * Replaces food data that exists in the database but have to be updated to comply with FatSecret API data storage time requirements
     * @param food food needing to be replaced
     */
    @Override
    public void replaceFood(Food food) {
        String sql = """
                UPDATE food
                SET food_id = ?,
                    common_name = ?,
                    food_type = ?,
                    calories = ?,
                    carbohydrates = ?,
                    protein = ?,
                    fat = ?,
                    serving_description = ?,
                    serving_id = ?,
                    retrieval_time = CURRENT_TIMESTAMP
                WHERE food_id = ?;
                """;
        try (Connection conn = databaseManager.connect();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, food.getId());
            ps.setString(2, food.getName());
            ps.setString(3, food.getType());
            ps.setDouble(4, food.getCalories());
            ps.setDouble(5, food.getCarbohydrates());
            ps.setDouble(6, food.getProtein());
            ps.setDouble(7, food.getFat());
            ps.setString(8, food.getServingDescription());
            ps.setInt(9, food.getServingId());
            ps.setInt(10, food.getId());


            int rowsUpdated = ps.executeUpdate();
            if (rowsUpdated == 0) {
                add(food);
            }
        } catch (SQLException e) {
            LOG.error(e);
        }
    }
}
