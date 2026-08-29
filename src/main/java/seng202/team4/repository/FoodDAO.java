package seng202.team4.repository;

import org.apache.commons.lang3.NotImplementedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team4.models.Food;
import seng202.team4.models.TreeModel;

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
    public List<Food> getAll() {
        List<Food> food = new ArrayList<>();
        String sql = "SELECT * FROM food WHERE common_name=?";
        try (Connection conn = databaseManager.connect();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)
        ) {
            while (rs.next()) {
                food.add(new Food(
                        rs.getString("common_name"),
                        rs.getString("food_type"),
                        rs.getString("nutritional_information")
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
     * @return name of food if success,
     */
    public String add(Food toAdd) {
        String sql = """
                INSERT INTO food (
                common_name, 
                food_type,
                nutritional_information
                ) VALUES (?, ?, ?)
                """;
        try (Connection conn = databaseManager.connect();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, toAdd.getName());
            ps.setString(2, toAdd.getType());
            ps.setString(3, toAdd.getNutritionalInformation());

            ps.executeUpdate();
            return toAdd.getName();
        } catch (SQLException sqlException) {
            LOG.error(sqlException);
            return null;
        }
    }

    /**
     * Deletes a specific food group from database
     * @param commonName of food group to delete
     */
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
                            rs.getString("common_name"),
                            rs.getString("food_type"),
                            rs.getString("nutritional_information")
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
    public Food getFoodbyTreeId(int treeId) {
        Food food = null;
        String sql = "SELECT * FROM food where common_name =(SELECT type FROM trees where id=?)";
        try (Connection conn = databaseManager.connect();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    food = new Food (
                            rs.getString("common_name"),
                            rs.getString("food_type"),
                            rs.getString("nutritional_information")
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
     * @param id id of object to get
     * (this object must be able to identify itself and its previous self)
     */
    @Override
    public Food getOne (int id) {
        throw new NotImplementedException();
    }


    /**
     * Not implemented
     * @param toAdd object to add
     */
    @Override
    public int add(TreeModel toAdd) {
        throw new NotImplementedException();
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
     * Not implemented
     * @param id object to be deleted from database
     */
    @Override
    public void delete (int id) {
        throw new NotImplementedException();
    }
}
