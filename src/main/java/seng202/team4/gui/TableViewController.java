package seng202.team4.gui;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team4.models.TreeModel;
import seng202.team4.services.StateManager;
import seng202.team4.services.TreeService;

import java.util.List;

/**
 * Controls tableview.fxml
 * @author George Mounce
 */
public class TableViewController extends ScreenController {

    private static final Logger log = LogManager.getLogger(TableViewController.class);

    private final TreeService treeService;

    @FXML
    private TableView<TreeModel> treeTableView;

    /**
     * Initialise the window
     *
     * @param stateManager Reference for state manager
     */
    public TableViewController(StateManager stateManager) {
        super(stateManager);
        treeService = new TreeService();

    }

    public String getFxmlFile() {
        return "/fxml/tableview.fxml";
    }

    public String getTitle() {
        return "Table View";
    }

    /**
     * Method to initialize table after constructor
     */
    @FXML
    private void initialize() {
        initTable();
    }

    /**
     * Method to send user back to main screen
     */
    @FXML
    private void onBackClicked() {
        getStateManager().getWindowManager().launchMainScreen(getStateManager());
    }

    /**
     * Initialise table of trees with ID, Ownership and Genus
     */
    private void initTable() {
        TableColumn<TreeModel, Integer> idCol = new TableColumn<>("Tree Id");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        //TableColumn<TreeModel, Float> coordsCol = new TableColumn<>("Coordinates");
        //coordsCol.setCellValueFactory(new PropertyValueFactory<>("coordinates"));

        TableColumn<TreeModel, String> ownerCol = new TableColumn<>("Ownership");
        ownerCol.setCellValueFactory(new PropertyValueFactory<>("ownership"));

        TableColumn<TreeModel, String> genusCol = new TableColumn<>("Genus");
        genusCol.setCellValueFactory(new PropertyValueFactory<>("genus"));

        treeTableView.getColumns().add(idCol);
        //treeTableView.getColumns().add(coordsCol);
        treeTableView.getColumns().add(ownerCol);
        treeTableView.getColumns().add(genusCol);

        List<TreeModel> trees = treeService.getAllTrees();


        treeTableView.setItems(
                FXCollections.observableArrayList(
                        trees
                )
        );
    }
}
