package seng202.team4.gui;

import java.util.function.Consumer;

/**
 * Handles requests from GUI controllers to change scenes, calls FXWrapper methods
 */
public class WindowManager {

    /**
     * stores launcher method
     */
    private final Consumer<WindowManager> mainLauncher;

    private final Consumer<WindowManager> tableViewLauncher;
    /**
     * stores clear scene method
     */
    private final Runnable clearScreen;

    /**
     * Constructor for window manager
     * @param mainLauncher method to load the main screen
     * @param clearScreen method to clear the scene
     */
    public WindowManager(
            Consumer<WindowManager> mainLauncher,
            Consumer<WindowManager> tableViewLauncher,
            Runnable clearScreen) {
        this.mainLauncher = mainLauncher;
        this.tableViewLauncher = tableViewLauncher;
        this.clearScreen = clearScreen;
        launchMainScreen();


    }

    /**
     * Launches main screen
     */
    public void launchMainScreen() { mainLauncher.accept(this);}

    /**
     * Closes main screen
     */
    public void closeMainScreen() {
        clearScreen.run();
    }

    public void launchTableView() {tableViewLauncher.accept(this);}
    public void closeTableView() { clearScreen.run();}
}
