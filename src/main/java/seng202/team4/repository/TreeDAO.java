package seng202.team4.repository;

import org.apache.commons.lang3.NotImplementedException;
import seng202.team4.models.TreeModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * DAOInterface implementation for trees, handles tree related actions to database
 */
public class TreeDAO implements TreeRepository {
    private static final Logger LOG = LogManager.getLogger(TreeDAO.class);

    private final DatabaseManager databaseManager;

    /**
     * Constructor
     * @param databaseManager means of connecting to the database
     */
    public TreeDAO(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    /**
     * Method to return all trees stored in the database
     * @return returns a list of TreeModels, empty list if failed
     */
    @Override
    public List<TreeModel> getAll() {
        List<TreeModel> trees = new ArrayList<>();
        String sql = """
                SELECT id, x, y, ownership, genus, food, common_name, last_modified, service_status
                FROM trees
                """;
        try (Connection conn = databaseManager.connect();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)
        ) {
            while (rs.next()) {
                float[] coords = new float[] {
                        rs.getFloat("x"),
                        rs.getFloat("y")
                };

                Timestamp timestamp = rs.getTimestamp("last_modified");
                LocalDateTime lastUpdated = retrieveTimestamp(timestamp);

                trees.add(new TreeModel(
                        rs.getInt("id"),
                        coords,
                        rs.getString("ownership"),
                        rs.getString("genus"),
                        rs.getString("food"),
                        rs.getString("common_name"),
                        lastUpdated,
                        rs.getString("service_status")
                        ));
            }
            return trees;
        } catch (SQLException sqlException) {
            LOG.error(sqlException);
            return new ArrayList<>();
        }
    }

    @Override
    public TreeModel getOne(Integer primaryKey) {
        return getOne(primaryKey.intValue());
    }

    /**
     * Method to return the tree model of choice by id
     * @param id id of object to get
     * @return TreeModel object of specific id, null if failed
     */
    public TreeModel getOne(int id) {
        TreeModel tree = null;
        String sql = "SELECT * FROM trees WHERE id=?";
        try (Connection conn = databaseManager.connect();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    float[] coords = new float[] {
                            rs.getFloat("x"),
                            rs.getFloat("y")
                    };

                    Timestamp timestamp = rs.getTimestamp("last_modified");
                    LocalDateTime lastUpdated = retrieveTimestamp(timestamp);

                    tree = new TreeModel(
                            rs.getInt("id"),
                            coords,
                            rs.getString("ownership"),
                            rs.getString("genus"),
                            rs.getString("food"),
                            rs.getString("common_name"),
                            lastUpdated,
                            rs.getString("service_status")
                    );

                }
            }
        } catch (SQLException sqlException) {
            LOG.error(sqlException);
            return null;
        }
        return tree;



    }

    /**
     * Add a tree object to the database
     * @param toAdd tree object to add
     * @return id of tree if success, -1 if failed
     */
    @Override
    public int add(TreeModel toAdd) {
        String sql = """
                    INSERT INTO trees (
                        id,
                        x,
                        y,
                        ownership,
                        genus,
                        food,
                        common_name,
                        last_modified,
                        service_status
                    ) VALUES (?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, ?);
                    """;

        try (Connection conn = databaseManager.connect();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, toAdd.getId());
            ps.setFloat(2, toAdd.getXCoord());
            ps.setFloat(3, toAdd.getYCoord());
            ps.setString(4, toAdd.getOwnership());
            ps.setString(5, toAdd.getGenus());
            ps.setString(6, toAdd.getFood());
            ps.setString(7, toAdd.getCommonName());
            ps.setString(8, toAdd.getServiceStatus());

            ps.executeUpdate();
            return toAdd.getId();
        } catch (SQLException sqlException) {
            LOG.error(sqlException);
            return -1;
        }
    }

    /**
     * delete a specific tree from database
     * @param id id of object to delete
     */
    @Override
    public void delete (Integer id) {
        String sql = "DELETE FROM trees WHERE id=?";
        try (Connection conn = databaseManager.connect();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException sqlException) {
            LOG.error(sqlException);
        }
    }

    /**
     * Not implemented
     * @param toUpdate Object that needs to be updated
     * (this object must be able to identify itself and its previous self)
     */
    @Override
    public void update (TreeModel toUpdate) {
        throw new NotImplementedException();
    }

    /**
     * retrieve all trees of a specified common name
     * @param type the common name specified
     * @return all trees with the common name
     */
    public List<TreeModel> getTreesByCommonName(String type) {
        List<TreeModel> trees = new ArrayList<>();
        String sql = "SELECT * FROM trees WHERE common_name=?";
        try (Connection conn = databaseManager.connect();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, type);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    float[] coords = new float[] {
                            rs.getFloat("x"),
                            rs.getFloat("y")
                    };

                    Timestamp timestamp = rs.getTimestamp("last_modified");
                    LocalDateTime lastUpdated = retrieveTimestamp(timestamp);

                    trees.add(new TreeModel(
                            rs.getInt("id"),
                            coords,
                            rs.getString("ownership"),
                            rs.getString("genus"),
                            rs.getString("food"),
                            rs.getString("common_name"),
                            lastUpdated,
                            rs.getString("service_status")

                    ));
                }
            }
            return trees;
        } catch (SQLException sqlException) {
            LOG.error(sqlException);
            return new ArrayList<>();
        }
    }

    /**
     * Method to get trees that are within a certain region and of a type
     * @param food Type of tree to look for, should be a mapped food from
     * {@link seng202.team4.services.FoodMapper} e.g "Apple" or "Walnut"
     * @param north north
     * @param east east
     * @param south south
     * @param west west
     * @return list of trees of type in the region
     */
    public List<TreeModel> getTreesByRegion(String food, double north, double east, double south, double west) {
        List<TreeModel> trees = new ArrayList<>();
        String query = """
                SELECT *
                FROM trees
                WHERE food = ? AND (x BETWEEN ? AND ?) AND (y BETWEEN ? AND ?);
                """;
        try (Connection conn = databaseManager.connect();
                 PreparedStatement ps = conn.prepareStatement(query)
        ) {
            ps.setString(1, food);
            ps.setDouble(2, west);
            ps.setDouble(3, east);
            ps.setDouble(4, south);
            ps.setDouble(5, north);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    float[] coords = new float[] {
                            rs.getFloat("x"),
                            rs.getFloat("y")
                    };

                    Timestamp timestamp = rs.getTimestamp("last_modified");
                    LocalDateTime lastUpdated = retrieveTimestamp(timestamp);

                    trees.add(new TreeModel(
                            rs.getInt("id"),
                            coords,
                            rs.getString("ownership"),
                            rs.getString("genus"),
                            rs.getString("food"),
                            rs.getString("common_name"),
                            lastUpdated,
                            rs.getString("service_status")
                    ));
                }
            }
            return trees;
        } catch (SQLException sqlException) {
            LOG.error(sqlException);
            return new ArrayList<>();
        }
    }

    /**
     * retrieve all trees of a specified common name
     * @param food the common name specified
     * @return all trees with the common name
     */
    public List<TreeModel> getTreesByFoodName(String food) {
        List<TreeModel> trees = new ArrayList<>();
        String sql = "SELECT * FROM trees WHERE food=?";
        try (Connection conn = databaseManager.connect();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, food);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    float[] coords = new float[] {
                            rs.getFloat("x"),
                            rs.getFloat("y")
                    };

                    Timestamp timestamp = rs.getTimestamp("last_modified");
                    LocalDateTime lastUpdated = retrieveTimestamp(timestamp);

                    trees.add(new TreeModel(
                            rs.getInt("id"),
                            coords,
                            rs.getString("ownership"),
                            rs.getString("genus"),
                            rs.getString("food"),
                            rs.getString("common_name"),
                            lastUpdated,
                            rs.getString("service_status")
                    ));
                }
            }
            return trees;
        } catch (SQLException sqlException) {
            LOG.error(sqlException);
            return new ArrayList<>();
        }

    }

    /**
     * Retrieves the given timestamp as a LocalDateTime object
     * @param timestamp the timestamp for when a trees  data was last updated
     * @return the given time stamp as a LocalDateTime
     */
    private LocalDateTime retrieveTimestamp(Timestamp timestamp) {
        LocalDateTime lastUpdated;
        if (timestamp != null) {
            lastUpdated = timestamp.toLocalDateTime();
        } else {
            lastUpdated = null;
        }
        return lastUpdated;
    }
}
