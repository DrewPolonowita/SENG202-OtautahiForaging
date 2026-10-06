package seng202.team4.gui.screens;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.VBox;
import javafx.scene.control.TableView;
import javafx.scene.control.Button;
import javafx.scene.control.cell.PropertyValueFactory;
import seng202.team4.gui.ScreenController;
import seng202.team4.gui.FilterManager;
import seng202.team4.gui.utilities.FruitFilterUtilities;
import seng202.team4.models.TreeModel;
import seng202.team4.services.StateManager;
import seng202.team4.services.dataCentre.treeData.TreeDataManager;
import seng202.team4.models.FilterCategory;
import javafx.scene.layout.HBox;
import java.util.ArrayList;
import java.util.List;

/**
 * Controls table_view.fxml
 * @author Team 4
 */
public class TableViewController extends ScreenController {


    private final TreeDataManager treeService;

    private final FilterManager filterManager;

    private List<TreeModel> filteredTrees = new ArrayList<>();

    @FXML
    private TableView<TreeModel> treeTableView;

    @FXML
    private VBox filterList;

    @FXML
    private HBox filterActions;

    /**
     * Initialise the window
     *
     * @param stateManager Reference for state manager
     */
    public TableViewController(StateManager stateManager) {
        super(stateManager);
        treeService = stateManager.getTreeService();
        filterManager = stateManager.getFilterManager();

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

        TableColumn<TreeModel, String> ownerCol = new TableColumn<>("Ownership");
        ownerCol.setCellValueFactory(new PropertyValueFactory<>("ownership"));

        TableColumn<TreeModel, String> genusCol = new TableColumn<>("Genus");
        genusCol.setCellValueFactory(new PropertyValueFactory<>("genus"));

        TableColumn<TreeModel, String> fruitCol = new TableColumn<>("Food Type");
        fruitCol.setCellValueFactory(new PropertyValueFactory<>("food"));

        TableColumn<TreeModel, String> nameCol = new TableColumn<>("Common Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("commonName"));

        treeTableView.getColumns().add(idCol);
        treeTableView.getColumns().add(ownerCol);
        treeTableView.getColumns().add(genusCol);
        treeTableView.getColumns().add(fruitCol);
        treeTableView.getColumns().add(nameCol);
        List<TreeModel> trees = treeService.getAllTrees();

        reloadTable();
    }

    /**
     * Method to toggle visibility of the filters when the filter button is clicked
     */
    @FXML
    private void onFilterClicked() {
        filterList.setVisible(!filterList.isVisible());
        filterActions.setVisible(filterList.isVisible());
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
     *For each fruit name in the enum FilterCategory, a filter button is created
     */
    private void createFruitButtons() {
        filterList.getChildren().clear();
        for (FilterCategory filterCategory : FilterCategory.values()) {
            Button filterButton = FruitFilterUtilities.createFruitButton(filterCategory);
            if (filterManager.isSelected(filterCategory)) {
                FruitFilterUtilities.toggleButtonStyle(filterButton);
            }
            filterButton.setOnAction(event -> filterButtonClicked(filterButton, filterCategory));
            filterList.getChildren().add(filterButton);
        }
    }

    /**
     *Toggles the fruit button being clicked, and updates the
     *table contents to show only the relevant fruit trees
     * @param button
     * @param filterCategory
     */
    public void filterButtonClicked(Button button, FilterCategory filterCategory) {
        FruitFilterUtilities.toggleButtonStyle(button);

        String fruitName = FruitFilterUtilities.formatFruitName(filterCategory);

        if (FruitFilterUtilities.isSelected(button)) {
            filteredTrees.addAll(treeService.getTreesByFood(fruitName));
        } else {
            filteredTrees.removeAll(treeService.getTreesByFood(fruitName));
        }

        filterManager.toggleFilters(filterCategory);
        reloadTable();
    }

    /**
     * Method to reload the table data based on filters applied
     * When no filter is applied defaults back to showing all trees
     */
    private void reloadTable() {
        List<TreeModel> filteredTrees = new ArrayList<>();

        for (FilterCategory filterCategory : filterManager.getSelectedFilters()) {
            String fruitName = FruitFilterUtilities.formatFruitName(filterCategory);
            filteredTrees.addAll(treeService.getTreesByFood(fruitName));
        }
        treeTableView.setItems(
                FXCollections.observableArrayList(
                        filteredTrees));
    }

    /**
     * For all buttons in FilterCategory, loop through to find buttons selected
     * and deselect all.
     * Update the recipes accordingly to the filters.
     */
    @FXML
    private void deselectAllFilters() {
        filterManager.deselectAllFilters();
        for (int i = 0; i < filterList.getChildren().size(); i++) {
            Button button = (Button) filterList.getChildren().get(i);
            if (FruitFilterUtilities.isSelected(button)) {
                FruitFilterUtilities.toggleButtonStyle(button);
            }
        }
        reloadTable();
    }

    /**
     * For all buttons in FilterCategory, loop through to find buttons unselected
     * and select all.
     * Update the recipes accordingly to the filters.
     */
    @FXML
    private void selectAllFilters() {
        filterManager.deselectAllFilters();
        for (int i = 0; i < filterList.getChildren().size(); i++) {
            Button button = (Button) filterList.getChildren().get(i);
            if (!FruitFilterUtilities.isSelected(button)) {
                FruitFilterUtilities.toggleButtonStyle(button);
            }
        }
        reloadTable();
    }
}
