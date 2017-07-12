package de.swm.wetterstation.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Created by luca on 05.07.2017.
 */
public class main extends Application{
    private FxController controller;

    public static void main(String args[]) {
        launch(args);


    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui.fxml"));
        Parent parent = loader.load();
        controller = loader.getController();

        primaryStage.setTitle("Wetterstation");
        primaryStage.setScene(new Scene(parent));
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        controller.saveProperties();
    }
}

