package seng202.team4.repository;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team4.exceptions.DatabaseAccessException;
import seng202.team4.models.ThemeSettings;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

/**
 * DAO implementation for the theme, handles saving and loading the theme in the database
 */
public class ThemeDAO implements ThemeRepository {
    private static final Logger LOG = LogManager.getLogger(ThemeDAO.class);
    private final DatabaseManager databaseManager;

    /**
     * Creates a new instance with a {@link DatabaseManager}
     * Also creates the theme table if it does not exist, so existing databases are updated
     *
     * @param databaseManager the database manager
     */
    public ThemeDAO(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
        try (Connection conn = databaseManager.connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS theme (
                        id INTEGER PRIMARY KEY CHECK (id = 1),
                        primary_color TEXT NOT NULL,
                        dark_mode INTEGER NOT NULL CHECK (dark_mode IN (0, 1))
                    )
                    """);
        } catch (SQLException error) {
            LOG.error(error);
        }
    }

    /**
     * Loads the saved theme from the database
     * @return the saved theme, or empty if none has been saved yet
     */
    @Override
    public Optional<ThemeSettings> load() {
        String sql = "SELECT primary_color, dark_mode FROM theme WHERE id = 1";
        try (Connection conn = databaseManager.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return Optional.of(new ThemeSettings(rs.getString("primary_color"), rs.getBoolean("dark_mode")));
            }
        } catch (SQLException error) {
            LOG.error(error);
        }
        return Optional.empty();
    }

    /**
     * Saves the theme to the database, replacing any previously saved theme
     * @param primaryColor the primary colour as a hex string
     * @param darkMode true if dark mode is enabled
     */
    @Override
    public void save(String primaryColor, boolean darkMode) {
        String sql = """
                INSERT INTO theme (id, primary_color, dark_mode) VALUES (1, ?, ?)
                ON CONFLICT(id) DO UPDATE SET primary_color = excluded.primary_color,
                    dark_mode = excluded.dark_mode;
                """;
        try (Connection conn = databaseManager.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, primaryColor);
            ps.setBoolean(2, darkMode);
            ps.executeUpdate();
        } catch (SQLException error) {
            throw new DatabaseAccessException("Failed to save theme", error);
        }
    }
}