package seng202.team4.repository;


import org.apache.commons.lang3.NotImplementedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team4.models.Food;


import java.sql.*;
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
        String sql = "SELECT * FROM food WHERE common_name=?";
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
                   serving_id
                   ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
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
     * @param commonName of food group to delete
     */
    @Override
    public void delete (String commonName) {
        String sql = "DELETE FROM food WHERE common_name=?";
        try (Connection conn = databaseManager.connect();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, commonName);
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
            ps.setString(2, type);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
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
        String sql = "SELECT * FROM food where common_name =(SELECT common_name FROM trees where id=?)";
        try (Connection conn = databaseManager.connect();
                PreparedStatement ps = conn.prepareStatement(sql)) {


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
     * @param commonName the commonName of the food model being retrieved
     * @return food model with common Name given, or null if fails
     */
    @Override
    public Food getOne (String commonName) {
        Food food = null;
        String sql = "SELECT * FROM food WHERE common_name=?";
        try (Connection conn = databaseManager.connect();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, commonName);
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
}
