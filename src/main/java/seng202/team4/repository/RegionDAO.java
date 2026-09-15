package seng202.team4.repository;

import org.apache.commons.lang3.NotImplementedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team4.exceptions.DuplicateEntryException;
import seng202.team4.models.Region;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * Placeholder
 */
public class RegionDAO implements RegionRepository {
    private static final Logger LOG = LogManager.getLogger(RegionDAO.class);
    private final DatabaseManager databaseManager;

    private static final int DUPLICATE_ITEM_CODE = 19;

    /**
     * Creates a new instance with a {@link DatabaseManager}
     *
     * @param databaseManager the database manager
     */
    public RegionDAO(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    @Override
    public Region getOne(Region region) {
        throw new NotImplementedException();
    }

    @Override
    public List<Region> getAll() {
        String sql = "SELECT * FROM REGIONS";

        List<Region> regions = new ArrayList<>();

        try (Connection conn = databaseManager.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)
        ) {
            while (rs.next()) {
                Region region = new Region(
                        rs.getString("type"),
                        rs.getDouble("north"),
                        rs.getDouble("east"),
                        rs.getDouble("south"),
                        rs.getDouble("west")
                );
                LocalDateTime dateTime = rs.getObject("last_updated", LocalDateTime.class);

                region.setLastUpdated(dateTime);
                regions.add(region);
            }

            return regions;
        } catch (SQLException sqlException) {

            LOG.error(sqlException);
            return new ArrayList<>();
        }
    }

    /**
     * Creates a new region in the database
     *
     * @param region object of type {@link Region} to add
     * @return the object insert id if added correctly
     * @throws DuplicateEntryException if the object already exists
     */
    @Override
    public int add(Region region) throws DuplicateEntryException {
        String sql = """
                    INSERT INTO REGIONS (
                        NORTH, EAST, SOUTH, WEST, LAST_UPDATED, TYPE
                    ) VALUES (?, ?, ?, ?, ?, ?);
                    """;

        try (
                Connection connection = databaseManager.connect();
                PreparedStatement ps = connection.prepareStatement(sql)
        ) {
            ps.setDouble(1, region.getNorthBound());
            ps.setDouble(2, region.getEastBound());
            ps.setDouble(3, region.getSouthBound());
            ps.setDouble(4, region.getWestBound());
            ps.setObject(5, LocalDateTime.now());
            ps.setString(6, region.getType());

            ps.executeUpdate();
            return 1;

        } catch (SQLException sqlException) {
            if (sqlException.getErrorCode() == DUPLICATE_ITEM_CODE) {
                return -2;
            }

            LOG.error(sqlException);
            return -1;
        }
    }

    /**
     * Placeholder
     * @param region primary key of object to delete
     */
    @Override
    public void delete(Region region) {
        String sql = """
                    DELETE FROM REGIONS
                    WHERE NORTH = ? AND EAST = ? AND SOUTH = ? AND WEST = ? AND TYPE = ?
                    """;
        try (
                Connection connection = databaseManager.connect();
                PreparedStatement ps = connection.prepareStatement(sql)
        ) {
            ps.setDouble(1, region.getNorthBound());
            ps.setDouble(2, region.getEastBound());
            ps.setDouble(3, region.getSouthBound());
            ps.setDouble(4, region.getWestBound());
            ps.setString(5, region.getType());

            ps.executeUpdate();

        } catch (SQLException sqlException) {
            LOG.error(sqlException);
        }
    }

    @Override
    public Region getRegion(Region region) {
        String sql = """
                    SELECT LAST_UPDATED FROM REGIONS
                    WHERE NORTH = ? AND EAST = ? AND SOUTH = ? AND WEST = ? AND TYPE = ?
                    """;
        try (
                Connection connection = databaseManager.connect();
                PreparedStatement ps = connection.prepareStatement(sql)
        ) {
            ps.setDouble(1, region.getNorthBound());
            ps.setDouble(2, region.getEastBound());
            ps.setDouble(3, region.getSouthBound());
            ps.setDouble(4, region.getWestBound());
            ps.setString(5, region.getType());

            try (ResultSet rs = ps.executeQuery()) {
                rs.next(); // Moves the cursor to your guaranteed single row
                region.setLastUpdated(rs.getObject("LAST_UPDATED", LocalDateTime.class));
                return region;
            }

        } catch (SQLException sqlException) {
            LOG.error(sqlException);
        }

        return null;
    }

    @Override
    public void update(Region region) {
        String sql = """
                    UPDATE REGIONS
                    SET LAST_UPDATED = ?
                    WHERE NORTH = ? AND EAST = ? AND SOUTH = ? AND WEST = ? AND TYPE = ?
                    """;

        try (
                Connection connection = databaseManager.connect();
                PreparedStatement ps = connection.prepareStatement(sql)
        ) {
            ps.setObject(1, region.getLastUpdated());
            ps.setDouble(2, region.getNorthBound());
            ps.setDouble(3, region.getEastBound());
            ps.setDouble(4, region.getSouthBound());
            ps.setDouble(5, region.getWestBound());
            ps.setString(6, region.getType());

            ps.executeUpdate();

        } catch (SQLException sqlException) {
            LOG.error(sqlException);
        }
    }
}
