package seng202.team4.services;

import seng202.team4.gui.WindowManager;
import seng202.team4.repository.*;

/**
 * The class which stores information related to the state of the entire app in a given instance
 * This class also has the ability to launch screens using the {@link WindowManager}
 * @author Team 4
 */
public class StateManager {

    private final WindowManager windowManager;
    private final DatabaseManager databaseManager;
    private final TreeRepository treeRepository;
    private final TreeService treeService;
    private final NoteRepository noteRepository;
    private final NoteService noteService;


    /**
     * A constructor which launches the mainScreen of the application
     * Initialises database manager passing in null, database manager will handle path
     * @param windowManager the window manager for this application
     *
     */
    public StateManager(WindowManager windowManager) {
        this.windowManager = windowManager;
        this.databaseManager = new DatabaseManager(null);

        this.treeRepository = new TreeDAO(databaseManager);
        this.treeService = new TreeService(treeRepository);

        this.noteRepository = new NoteDAO(databaseManager);
        this.noteService = new NoteService(treeRepository);


        this.windowManager.launchMapScreen(this);
        this.windowManager.launchNavScreen(this);

    }

    public WindowManager getWindowManager() {
        return windowManager;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public TreeService getTreeService() {
        return treeService;
    }
}
