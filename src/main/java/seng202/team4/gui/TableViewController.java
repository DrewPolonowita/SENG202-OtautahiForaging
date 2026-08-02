package seng202.team4.gui;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team4.models.Tree;
import seng202.team4.services.CounterService;

public class TableViewController {

    private static final Logger log = LogManager.getLogger(TableViewController.class);

    private final WindowManager windowManager;

    @FXML
    private TableView<Tree> treeTableView;

    /**
     * Initialise the window
     *
     * @param windowManager Reference for window manager
     */
    public TableViewController(WindowManager windowManager) {
        this.windowManager = windowManager;
        initTable();
    }


    private void initTable() {
        TableColumn<Tree, String> itemCol = new TableColumn<>("Tree Id");
        itemCol.setCellValueFactory(new PropertyValueFactory<>("Id"));

        TableColumn<Tree, Float> coordsCol = new TableColumn<>("Coordinates");
        coordsCol.setCellValueFactory(new PropertyValueFactory<>("coordinates"));

        TableColumn<Tree, String> ownerCol = new TableColumn<>("Ownership");
        ownerCol.setCellValueFactory(new PropertyValueFactory<>("ownership"));

        TableColumn<Tree, String> genusCol = new TableColumn<>("Genus");
        genusCol.setCellValueFactory(new PropertyValueFactory<>("genus"));

        treeTableView.getColumns().add(itemCol);
        treeTableView.getColumns().add(coordsCol);
        treeTableView.getColumns().add(ownerCol);
        treeTableView.getColumns().add(genusCol);
    }
}
