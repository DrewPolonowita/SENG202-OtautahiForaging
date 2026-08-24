package seng202.team4;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team4.models.TreeModel;
import seng202.team4.services.treeData.TreeDataManager;
import seng202.team4.services.treeData.TreeService;

import java.util.List;

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
//        FXAppEntry.launch(FXAppEntry.class, args);
//        new DatabaseManager(null).resetDB();

        System.out.println("START");

        TreeDataManager manager = new TreeService();

        List<TreeModel> trees = manager.updateTreesByRegion("Apple", -43.4, 172.8, -43.8, 172.7);

        for (TreeModel tree: trees) {
            System.out.printf(
                    "(%f, %f)%n", tree.getXCoord(), tree.getYCoord()
            );
        }
    }
}
