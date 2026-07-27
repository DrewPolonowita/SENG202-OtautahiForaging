package seng202.team4.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Handles the logic for opening and closing GUI scenes
 */
public class FXWrapper {
    @FXML
    private BorderPane borderPane;
    private Stage stage;

    /**
     * Initialise FXWrapper, creates the window manager and feeds in the methods for use
     * @param stage primary stage of application
     */
    public void init(Stage stage) {
        this.stage = stage;
        new WindowManager(
                this::launchMain,
                this::clearPane
                // for each scene repeat this
        );
    }

    /**
     * Clears the scene for use by another scene
     */
    public void clearPane() {
        borderPane.getChildren().removeAll(borderPane.getChildren());
    }

    /**
     * Launches main screen
     * @param windowManager Used as the interface for switching between windows
     */
    public void launchMain(WindowManager windowManager){
        try {
            FXMLLoader setupLoader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));

            setupLoader.setControllerFactory(param -> new MainController(windowManager));
            Parent setupParent  = setupLoader.load();
            borderPane.setCenter(setupParent);
            stage.setTitle("App");
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
