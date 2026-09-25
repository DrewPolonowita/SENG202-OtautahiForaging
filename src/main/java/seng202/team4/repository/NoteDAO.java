package seng202.team4.repository;

import org.apache.commons.lang3.NotImplementedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team4.models.Note;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Class for accessing note related data in database
 */
public class NoteDAO implements NoteRepository {
    private static final DateTimeFormatter SQLITE_FORMAT =
            DateTimeFormatter.ofPattern("yyy-MM-dd HH:mm:ss");
    private static final Logger LOG = LogManager.getLogger(NoteDAO.class);
    private final DatabaseManager databaseManager;

    /**
     * Constructor
     */
    public NoteDAO(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    private Note mapRow(ResultSet rs) throws SQLException {
        LocalDateTime timeCreated = LocalDateTime.parse(rs.getString("time_created"), SQLITE_FORMAT);
        return new Note(
                rs.getInt("id"),
                rs.getString("name"),
                timeCreated,
                rs.getInt("tree_id"),
                rs.getString("description"),
                rs.getString("image_path")
        );
    }

    /**
     * Adds a note object to the database
     * @param toAdd object of type T to add
     * @return id of the key if successful or -1 if fault
     */
    public int add(Note toAdd) {
        String sql = """
                INSERT INTO note (time_created, name, tree_id, description, image_path)
                VALUES (datetime('now', 'localtime'), ?, ?, ?, ?);
                """;
        try (Connection conn = databaseManager.connect();
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, toAdd.getName());
            ps.setInt(2, toAdd.getTreeId());
            ps.setString(3, toAdd.getDescription());
            ps.setString(4, toAdd.getImagePath());

            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
            return -1;
        } catch (SQLException error) {
            LOG.error(error);
            return -1;
        }
    }

    /**
     * Gets all notes from database as note models and returns them as a list
     * @return a list of all notes stored in the database
     */
    @Override
    public List<Note> getAll() {
        List<Note> notes = new ArrayList<>();
        String sql = "SELECT * FROM note";
        try (Connection conn = databaseManager.connect();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                notes.add(mapRow(rs));
            }
            return notes;
        } catch (SQLException error) {
            LOG.error(error);
            return new ArrayList<>();
        }
    }

    /**
     * Get a note from the database
     * @param id the primary key of object to get
     * @return the note with id provided
     */
    @Override
    public Note getOne(Integer id) {
        Note note = null;
        String sql = "SELECT * FROM note WHERE id=?";
        try (Connection conn = databaseManager.connect();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    note = mapRow(rs);
                }
            }
        } catch (SQLException error) {
            LOG.error(error);
            return null;
        }
        return note;
    }

    /**
     * Deletes a note from the database
     * @param id of the note deleted from the database
     */
    @Override
    public void delete(Integer id) {
        String sql = "DELETE FROM note WHERE id=?";
        try (Connection conn = databaseManager.connect();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException error) {
            LOG.error(error);
        }
    }

    /**
     * Updates an existing note in the database
     * @param toUpdate the note to update
     */
    @Override
    public void update(Note toUpdate) {
        String sql = """
                UPDATE note
                SET name = ?, description = ?, image_path = ?, tree_id = ?
                WHERE id = ?;
                """;
        try (Connection conn = databaseManager.connect();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, toUpdate.getName());
            ps.setString(2, toUpdate.getDescription());
            ps.setString(3, toUpdate.getImagePath());
            ps.setInt(4, toUpdate.getTreeId());
            ps.setInt(5, toUpdate.getId());

            ps.executeUpdate();
        } catch (SQLException error) {
            LOG.error(error);
        }
    }

    /**
     * Gets notes associated with a specific tree using the trees unique id
     * @param treeId the tree's unique id
     * @return a list of all the notes made for a given tree, or an empty list if no notes are made
     */
    @Override
    public List<Note> getNotesByTreeId(int treeId) {
        List<Note> notes = new ArrayList<>();
        String sql = "SELECT * FROM note WHERE tree_id=?";
        try (Connection conn = databaseManager.connect();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, treeId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    notes.add(mapRow(rs));
                }
            }
            return notes;
        } catch (SQLException error) {
            LOG.error(error);
            return new ArrayList<>();
        }
    }
}
