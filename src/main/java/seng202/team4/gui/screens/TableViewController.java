package seng202.team4.gui.screens;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.VBox;
import javafx.scene.control.TableView;
import javafx.scene.control.Button;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team4.gui.ScreenController;
import seng202.team4.gui.utilities.FruitFilter;
import seng202.team4.models.TreeModel;
import seng202.team4.services.StateManager;
import seng202.team4.services.dataCentre.treeData.TreeDataManager;
import seng202.team4.models.Fruit;

import java.util.ArrayList;
import java.util.List;

/**
 * Controls table_view.fxml
 * @author Team 4
 */
public class TableViewController extends ScreenController {

    private static final Logger LOG = LogManager.getLogger(TableViewController.class);

    private final TreeDataManager treeService;

//    private boolean appleFiltered;
//
//    private boolean pearFiltered;
//
//    private boolean cherryFiltered;
//
//    private boolean peachFiltered;
//
//    private boolean apricotFiltered;
//
    private List<TreeModel> filteredTrees = new ArrayList<>();

    @FXML
    private TableView<TreeModel> treeTableView;

    @FXML
    private VBox filterList;

    /**
     * Initialise the window
     *
     * @param stateManager Reference for state manager
     */
    public TableViewController(StateManager stateManager) {
        super(stateManager);
        treeService = stateManager.getTreeService();

    }

    public String getFxmlFile() {
        return "/fxml/table_view.fxml";
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
        createFruitButtons();
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

        TableColumn<TreeModel, String> fruitCol = new TableColumn<>("Food Type");
        fruitCol.setCellValueFactory(new PropertyValueFactory<>("food"));

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
     * Creates a filter button for each fruit in the FRUIT enum.
     */
    private void createFruitButtons() {
        filterList.getChildren().clear();
        for (Fruit fruit : Fruit.values()) {
            Button filterButton = new Button("  " + fruit.name().charAt(0)
                    + fruit.name().substring(1).toLowerCase());
            filterButton.setMaxWidth(Double.MAX_VALUE);
            filterButton.setPrefHeight(35); // based on previous dimensions
            filterButton.getStyleClass().add("sideBar-button");
            filterButton.setOnAction(event -> filterButtonClicked(filterButton, fruit)
            );
            filterList.getChildren().add(filterButton);
        }

    }

    /**
     *Toggles the fruit button being clicked, and updates the
     *table contents to show only the relevant fruit trees
     * @param button
     * @param fruit
     */
    public void filterButtonClicked(Button button, Fruit fruit) {
        if (button.getStyleClass().contains("sideBar-button-selected")) {
            button.getStyleClass().remove("sideBar-button-selected");
        } else {
            button.getStyleClass().add("sideBar-button-selected");
        }
        String fruitName = fruit.name().charAt(0) + fruit.name().substring(1).toLowerCase();
        if (button.getStyleClass().contains("sideBar-button-selected")) {
            filteredTrees.addAll(treeService.getTreesByFood(fruitName));
        } else {
            filteredTrees.removeAll(treeService.getTreesByFood(fruitName));
        }

        reloadTable();
    }
//    /**
//     * Method to toggle apple tree filter
//     */
//    @FXML
//    private void appleFilterChecked() {
//        if (!appleFiltered) {
//            filteredTrees.addAll(treeService.getTreesByFood("Apple"));
//            appleFiltered = true;
//            reloadTable();
//        } else {
//            filteredTrees.removeAll(treeService.getTreesByFood("Apple"));
//            appleFiltered = false;
//            reloadTable();
//        }
//    }
//
//    /**
//     * Method to toggle pear tree filter
//     */
//    @FXML
//    private void pearFilterChecked() {
//        if (!pearFiltered) {
//            filteredTrees.addAll(treeService.getTreesByFood("Pear"));
//            pearFiltered = true;
//            reloadTable();
//        } else {
//            filteredTrees.removeAll(treeService.getTreesByFood("Pear"));
//            pearFiltered = false;
//            reloadTable();
//        }
//    }
//
//    /**
//     * Method to toggle cherry tree filter
//     */
//    @FXML
//    private void cherryFilterChecked() {
//        if (!cherryFiltered) {
//            filteredTrees.addAll(treeService.getTreesByFood("Cherry"));
//            cherryFiltered = true;
//            reloadTable();
//        } else {
//            filteredTrees.removeAll(treeService.getTreesByFood("Cherry"));
//            cherryFiltered = false;
//            reloadTable();
//        }
//    }
//
//    /**
//     * Method to toggle peach tree filter
//     */
//    @FXML
//    private void peachFilterChecked() {
//        if (!peachFiltered) {
//            filteredTrees.addAll(treeService.getTreesByFood("Peach"));
//            peachFiltered = true;
//            reloadTable();
//        } else {
//            filteredTrees.removeAll(treeService.getTreesByFood("Peach"));
//            peachFiltered = false;
//            reloadTable();
//        }
//    }
//
//    /**
//     * Method to toggle apricot tree filter
//     */
//    @FXML
//    private void apricotFilterChecked() {
//        if (!apricotFiltered) {
//            filteredTrees.addAll(treeService.getTreesByFood("Apricot"));
//            apricotFiltered = true;
//            reloadTable();
//        } else {
//            filteredTrees.removeAll(treeService.getTreesByFood("Apricot"));
//            apricotFiltered = false;
//            reloadTable();
//        }
//    }

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
