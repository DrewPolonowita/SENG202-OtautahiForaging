package seng202.team4.gui;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team4.models.TreeModel;
import seng202.team4.services.StateManager;
import seng202.team4.services.TreeService;

import java.util.ArrayList;
import java.util.List;

/**
 * Controls tableview.fxml
 * @author Team 4
 */
public class TableViewController extends ScreenController {

    private static final Logger LOG = LogManager.getLogger(TableViewController.class);

    private final TreeService treeService;

    private boolean appleFiltered;

    private boolean pearFiltered;

    private boolean cherryFiltered;

    private boolean peachFiltered;

    private boolean apricotFiltered;

    private List<TreeModel> filteredTrees = new ArrayList<>();

    @FXML
    private TableView<TreeModel> treeTableView;

    @FXML
    private GridPane filterList;

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
     * Method to initialize table after constructor and hide filter options
     */
    @FXML
    private void initialize() {
        initTable();
        hideFilterList();
    }

    /**
     * Method to send user back to main screen
     */
    @FXML
    private void onBackClicked() {
        getStateManager().getWindowManager().launchMapScreen(getStateManager());
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

        TableColumn<TreeModel, String> fruitCol = new TableColumn<>("Fruit Type");
        fruitCol.setCellValueFactory(new PropertyValueFactory<>("fruit"));

        TableColumn<TreeModel, String> nameCol = new TableColumn<>("Common Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("commonName"));

        treeTableView.getColumns().add(idCol);
        //treeTableView.getColumns().add(coordsCol);
        treeTableView.getColumns().add(ownerCol);
        treeTableView.getColumns().add(genusCol);
        treeTableView.getColumns().add(fruitCol);
        treeTableView.getColumns().add(nameCol);
        List<TreeModel> trees = treeService.getAllTrees();


        treeTableView.setItems(
                FXCollections.observableArrayList(
                        trees
                )
        );
    }

    /**
     * Method to toggle visibility of the filters when the filter button is clicked
     */
    @FXML
    private void onFilterClicked() {
        if (filterList.isVisible()) {
            hideFilterList();
        } else {
            showFilterList();
        }
    }

    /**
     * Method to hide filter list
     */
    @FXML
    private void hideFilterList() {
        filterList.setVisible(false);
    }

    /**
     * Method to hide filter list
     */
    @FXML
    private void showFilterList() {
        filterList.setVisible(true);
    }

    /**
     * Method to toggle apple tree filter
     */
    @FXML
    private void appleFilterChecked() {
        if (!appleFiltered) {
            filteredTrees.addAll(treeService.getAppleTrees());
            appleFiltered = true;
            reloadTable();
        } else {
            filteredTrees.removeAll(treeService.getAppleTrees());
            appleFiltered = false;
            reloadTable();
        }
    }

    /**
     * Method to toggle pear tree filter
     */
    @FXML
    private void pearFilterChecked() {
        if (!pearFiltered) {
            filteredTrees.addAll(treeService.getPearTrees());
            pearFiltered = true;
            reloadTable();
        } else {
            filteredTrees.removeAll(treeService.getPearTrees());
            pearFiltered = false;
            reloadTable();
        }
    }

    /**
     * Method to toggle cherry tree filter
     */
    @FXML
    private void cherryFilterChecked() {
        if (!cherryFiltered) {
            filteredTrees.addAll(treeService.getCherryTrees());
            cherryFiltered = true;
            reloadTable();
        } else {
            filteredTrees.removeAll(treeService.getCherryTrees());
            cherryFiltered = false;
            reloadTable();
        }
    }

    /**
     * Method to toggle peach tree filter
     */
    @FXML
    private void peachFilterChecked() {
        if (!peachFiltered) {
            filteredTrees.addAll(treeService.getPeachTrees());
            peachFiltered = true;
            reloadTable();
        } else {
            filteredTrees.removeAll(treeService.getPeachTrees());
            peachFiltered = false;
            reloadTable();
        }
    }

    /**
     * Method to toggle apricot tree filter
     */
    @FXML
    private void apricotFilterChecked() {
        if (!apricotFiltered) {
            filteredTrees.addAll(treeService.getApricotTrees());
            apricotFiltered = true;
            reloadTable();
        } else {
            filteredTrees.removeAll(treeService.getApricotTrees());
            apricotFiltered = false;
            reloadTable();
        }
    }

    /**
     * Method to reload the table data based on filters applied
     * When no filter is applied defaults back to showing all trees
     */
    private void reloadTable() {
        if (filteredTrees.isEmpty()) {
            List<TreeModel> trees = treeService.getAllTrees();
            treeTableView.setItems(
                    FXCollections.observableArrayList(
                            trees
                    ));
        } else {
            treeTableView.setItems(
                    FXCollections.observableArrayList(
                            filteredTrees
                    ));
        }
    }
}
