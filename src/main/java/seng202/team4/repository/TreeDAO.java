package seng202.team4.repository;

import org.apache.commons.lang3.NotImplementedException;
import seng202.team4.models.TreeModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TreeDAO implements DAOInterface<TreeModel> {
    private static final Logger log = LogManager.getLogger(TreeDAO.class);

    private final DatabaseManager databaseManager;
    public TreeDAO(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }
    @Override
    public List<TreeModel> getAll() {
       List<TreeModel> trees = new ArrayList<>();
       String sql = "SELECT * FROM sales";
        try (Connection conn = databaseManager.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                int[] coords = new int[] {
                        rs.getInt("x"),
                        rs.getInt("y")
                };
                trees.add(new TreeModel(
                        rs.getInt("id"),
                        coords,
                        rs.getString("ownership"),
                        rs.getString("genus"),
                        rs.getString("fruit"),
                        rs.getString("common_name")));
            }
            return trees;
        } catch (SQLException sqlException) {
            log.error(sqlException);
            return new ArrayList<>();
        }
    }
    @Override
    public TreeModel getOne(int id) {
        TreeModel tree = null;
        String sql = "SELECT * FROM trees WHERE id=?";
        try (Connection conn = databaseManager.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int[] coords = new int[] {
                            rs.getInt("x"),
                            rs.getInt("y")
                    };

                    tree = new TreeModel(
                            rs.getInt("id"),
                            coords,
                            rs.getString("ownership"),
                            rs.getString("genus"),
                            rs.getString("fruit"),
                            rs.getString("common_name")
                    );

                }
            }
        } catch (SQLException sqlException) {
            log.error(sqlException);
            return null;
        }
        return tree;



    }
    @Override
    public int add(TreeModel toAdd) {
        String sql = """
            INSERT INTO trees (
                id,
                x,
                y,
                ownership,
                genus,
                fruit,
                common_name
            ) VALUES (?, ?, ?, ?, ?, ?, ?);
            """;

        try (Connection conn = databaseManager.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, toAdd.getId());
            ps.setInt(2, toAdd.getXCoord());
            ps.setInt(3, toAdd.getYCoord());
            ps.setString(4, toAdd.getOwnership());
            ps.setString(5, toAdd.getGenus());
            ps.setString(6, toAdd.getFruit());
            ps.setString(7, toAdd.getCommonName());

            ps.executeUpdate();
            return toAdd.getId();
        } catch (SQLException sqlException) {
            log.error(sqlException);
            return -1;
        }
    }

    @Override
    public void delete (int id){
        String sql = "DELETE FROM trees WHERE id=?";
        try (Connection conn = databaseManager.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException sqlException) {
            log.error(sqlException);
        }
    }

    @Override
    public void update (TreeModel toUpdate){
        throw new NotImplementedException();
    }
}
