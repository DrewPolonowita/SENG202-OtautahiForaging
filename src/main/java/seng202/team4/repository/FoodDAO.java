package seng202.team4.repository;


import org.apache.commons.lang3.NotImplementedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team4.exceptions.DatabaseAccessException;
import seng202.team4.exceptions.DuplicateEntryException;
import seng202.team4.models.Food;
import seng202.team4.models.FoodServing;
import seng202.team4.services.FoodFactory;


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
    private static final String FOOD_ID_REFERENCE = "food_id";
    private static final String FOOD_TYPE_REFERENCE = "food_type";
    private static final String FOOD_COMMON_NAME_REFERENCE = "common_name";
    public static final String SERVING_ID_REFERENCE = "serving_id";
    public static final String SERVING_DESCRIPTION_REFERENCE = "serving_description";
    public static final String CALORIES_REFERENCE = "calories";
    public static final String CARBOHYDRATES_REFERENCE = "carbohydrates";
    public static final String PROTEIN_REFERENCE = "protein";
    public static final String FAT_REFERENCE = "fat";


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
     * @return returns a list of Foods
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
                food.add(createFoodFromResultSet(rs, conn));
            }
            return food;
        } catch (SQLException sqlException) {
            throw new DatabaseAccessException("Failed to get foods", sqlException);
        }
    }


    /**
     * Add a food object to the database
     * @param toAdd food object to add
     * @return unique food id of food if success, otherwise DatabaseAccessException
     */
    @Override
    public int add(Food toAdd) throws DuplicateEntryException {
        String sql = """
                   INSERT INTO food (
                   food_id,
                   common_name,
                   food_type,
                   retrieval_time
                   ) VALUES (?, ?, ?, CURRENT_TIMESTAMP)
                   """;
        try (Connection conn = databaseManager.connect();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, toAdd.getId());
            ps.setString(2, toAdd.getName());
            ps.setString(3, toAdd.getType());

            ps.executeUpdate();
            addServings(toAdd, conn);
            return toAdd.getId();
        } catch (SQLException sqlException) {
            if (sqlException.getMessage().contains("UNIQUE constraint failed")) {
                throw new DuplicateEntryException("Food exists with id " + toAdd.getId()
                        + " and name " + toAdd.getName(), sqlException);
            }
            throw new DatabaseAccessException("Failed to add food " + toAdd.getName(), sqlException);
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
            throw new DatabaseAccessException("Failed to delete food with id " + foodId, sqlException);
        }
    }


    /**
     * Method to get a list of food models of one shared type
     * @param type type of the object to get
     * @return food models of the specific type
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
                    foods.add(createFoodFromResultSet(rs, conn)
                    );
                }
            }
            return foods;
        } catch (SQLException sqlException) {
            throw new DatabaseAccessException("Failed to get foods of type " + type, sqlException);
        }
    }


    /**
     * Gets a food model matching the food grown on a particular tree
     * @param treeId the id of the specified tree
     * @return the food model corresponding to the food grown on the tree, or null if none exist
     */
    @Override
    public Food getFoodbyTreeId(int treeId) {
        Food food = null;
        String sql = "SELECT * FROM food WHERE common_name =(SELECT food FROM trees WHERE id=?)";
        try (Connection conn = databaseManager.connect();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, treeId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    food = createFoodFromResultSet(rs, conn);
                }
            }
            return food;
        } catch (SQLException sqlException) {
            throw new DatabaseAccessException("Failed to get food with tree id " + treeId, sqlException);
        }
    }




    /**
     * Gets a single food model from the database by its common name
     * @param foodId the id of the food being retrieved
     * @return food model with common Name given, or null none exist
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
                    food = createFoodFromResultSet(rs, conn);
                }
            }
            return food;
        } catch (SQLException sqlException) {
            throw new DatabaseAccessException("Failed to get food with id " + foodId, sqlException);
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
                    return rs.getInt(FOOD_ID_REFERENCE);
                }
            }
        } catch (SQLException sqlException) {
            throw new DatabaseAccessException("Failed to get food id for food name " + foodName, sqlException);
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

        } catch (SQLException sqlException) {
            throw new DatabaseAccessException("Failed to check food details for id" + foodId, sqlException);
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
                SET common_name = ?,
                    food_type = ?,
                    retrieval_time = CURRENT_TIMESTAMP
                WHERE food_id = ?;
                """;
        try (Connection conn = databaseManager.connect();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, food.getName());
            ps.setString(2, food.getType());
            ps.setInt(3, food.getId());


            int rowsUpdated = ps.executeUpdate();
            if (rowsUpdated == 0) {
                tryAddFood(food);
            } else {
                String deleteServingsSql = "DELETE FROM food_serving WHERE food_id=?";

                try (PreparedStatement deletePs = conn.prepareStatement(deleteServingsSql)) {
                    deletePs.setInt(1, food.getId());
                    deletePs.executeUpdate();
                }
                addServings(food, conn);
            }
        } catch (SQLException sqlException) {
            throw new DatabaseAccessException("Failed to replace food " + food.getName(), sqlException);
        }
    }

    private void tryAddFood(Food food) {
        try {
            int addedFoodId = add(food);

            if (addedFoodId != food.getId()) {
                throw new DatabaseAccessException(
                        "Failed to add replacement food " + food.getName(),
                        null
                );
            }
        } catch (DuplicateEntryException e) {
            LOG.error(e);
            throw new DatabaseAccessException("Failed to add replacement food " + food.getName(), e);
        }
    }


    private void addServings(Food food, Connection conn) throws SQLException {
        String sql = """
                INSERT OR IGNORE INTO food_serving (
                    food_id,
                    serving_id,
                    serving_description,
                    calories,
                    carbohydrates,
                    protein,
                    fat
                ) VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            for (FoodServing serving : food.getServings()) {
                ps.setInt(1, food.getId());
                ps.setInt(2, serving.getServingId());
                ps.setString(3, serving.getDescription());
                ps.setDouble(4, serving.getCalories());
                ps.setDouble(5, serving.getCarbohydrates());
                ps.setDouble(6, serving.getProtein());
                ps.setDouble(7, serving.getFat());
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException sqlException) {
            LOG.error("Failed to add servings", sqlException);
            throw sqlException;
        }


    }


    private List<FoodServing> getServings(int foodId, Connection conn) throws SQLException {
        String sql = """
                SELECT serving_id,
                       serving_description,
                       calories,
                       carbohydrates,
                       protein,
                       fat
                FROM food_serving
                WHERE food_id = ?
                ORDER BY serving_id
                """;

        List<FoodServing> servings = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, foodId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    servings.add(FoodFactory.createServing(
                            rs.getInt(SERVING_ID_REFERENCE),
                            rs.getString(SERVING_DESCRIPTION_REFERENCE),
                            rs.getDouble(CALORIES_REFERENCE),
                            rs.getDouble(CARBOHYDRATES_REFERENCE),
                            rs.getDouble(PROTEIN_REFERENCE),
                            rs.getDouble(FAT_REFERENCE)
                    ));
                }
            }
        }

        return servings;
    }


    private Food createFoodFromResultSet(ResultSet rs, Connection conn) throws SQLException {
        int foodId = rs.getInt(FOOD_ID_REFERENCE);

        return FoodFactory.create(
                foodId,
                rs.getString(FOOD_COMMON_NAME_REFERENCE),
                rs.getString(FOOD_TYPE_REFERENCE),
                getServings(foodId, conn)
        );
    }



}
