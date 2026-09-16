package seng202.team4.gui.bridge;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team4.models.TreeModel;

import java.util.function.Function;

/**
 * The class that bridges from javascript to java
 * The functions within can be called from javascript in the map view when we set an object of this class
 * as a member of the javascript
 */

public class JavaScriptBridge {

    private static final Logger LOG = LogManager.getLogger(JavaScriptBridge.class);
    private final AddTreeInterface addTreeInterface;
    private final TreeClickedInterface treeClickedInterface;
    private final AddToJournalInterface addToJournalInterface;

    private final MapMovedInterface mapMovedInterface;

    /**
     * Creates JavaScript bridge object with a callback lambda function for displaying the tree on the map after creation
     * @param addToJournalLambda add a note to the journal for the given tree id
     * @param addTreeLambda add tree lambda function to add tree to js map
     * @param getTreeLambda indicate a tree has been clicked to get from database by id, return true if its found and used.
     */
    public JavaScriptBridge(AddToJournalInterface addToJournalLambda, AddTreeInterface addTreeLambda, TreeClickedInterface getTreeLambda, MapMovedInterface mapMovedInterface) {
        addToJournalInterface = addToJournalLambda;
        addTreeInterface = addTreeLambda;
        treeClickedInterface = getTreeLambda;
        this.mapMovedInterface = mapMovedInterface;
    }

    /**
     * Creates a JavaScript bridge with default no-op callbacks
     */
    public JavaScriptBridge() {
        addToJournalInterface = (int i) -> { };
        addTreeInterface = (TreeModel tree) -> { };
        treeClickedInterface = (int i) -> false;
        mapMovedInterface = null;
    }

    /**
     * Takes the id of a tree and passes this to the TreeClickedInterface
     * @param id of tree
     * @return true or false depending on operations succession
     */
    public boolean getTreeFromClick(int id) {
        return treeClickedInterface.operation(id);
    }

    public void mapMoved(double north, double east, double south, double west) {
        assert mapMovedInterface != null;
        mapMovedInterface.mapMoved(north, east, south, west);
    }

    /**
     * Takes the id of a tree and passes this to the AddToJournalInterface
     * @param id id of tree to add to journal
     */
    public void addTreeToJournal(int id) {
        addToJournalInterface.operation(id);
    }

}
