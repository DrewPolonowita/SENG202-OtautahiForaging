package seng202.team4.gui.bridge;

/**
 * Interface to allow passing a lambda for adding a tree to the journal
 * @author Team 4
 */
public interface AddToJournalInterface {

    /**
     * Operation
     * @param id id of the tree to add to journal
     */
    void operation(int id);
}
