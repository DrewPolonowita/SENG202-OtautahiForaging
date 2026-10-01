package seng202.team4;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team4.gui.FXAppEntry;

/**
 * Default entry point class
 * @author seng202 teaching team
 */
public class App {
    private static final Logger LOG = LogManager.getLogger(App.class);

    /**
     * Entry point which runs the javaFX application
     * Also shows off some different logging levels
     * @param args program arguments from command line
     */
    void main(String[] args) {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        FXAppEntry.launch(FXAppEntry.class, args);
    }
}
