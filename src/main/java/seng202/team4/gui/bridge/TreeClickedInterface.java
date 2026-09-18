package seng202.team4.gui.bridge;

/**
 * Interface to retrieve the id of a tree so the database can fetch it
 * @author Team 4
 */
public interface TreeClickedInterface {
    /**
     * Operation
     * @param id tree id
     * @return true if successful
     */
    boolean operation(int id);
}
