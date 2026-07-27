package seng202.team4.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

public class FXWrapper {
    @FXML
    private BorderPane borderPane;
    private Stage stage;
    public void init(Stage stage) {
        this.stage = stage;
        new WindowManager(
                this::launchMain,
                this::clearPane
        );



    }

    public void clearPane() {
        borderPane.getChildren().removeAll(borderPane.getChildren());
    }

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
