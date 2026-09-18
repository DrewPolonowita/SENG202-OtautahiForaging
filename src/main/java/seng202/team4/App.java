package seng202.team4;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team4.gui.FXAppEntry;
import seng202.team4.repository.DatabaseManager;
import seng202.team4.repository.RegionDAO;
import seng202.team4.repository.TreeDAO;
import seng202.team4.services.FoodMapper;
import seng202.team4.services.api.ApiClient;
import seng202.team4.services.api.TreeApiClient;
import seng202.team4.services.dataCentre.treeData.RegionService;
import seng202.team4.services.dataCentre.treeData.TreeDataManager;
import seng202.team4.services.dataCentre.treeData.TreeService;

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
        FXAppEntry.launch(FXAppEntry.class, args);
    }
}
