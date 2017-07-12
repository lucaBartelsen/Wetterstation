package de.swm.wetterstation;

import com.tinkerforge.IPConnection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Created by luca on 05.07.2017.
 */
public class main extends Application{
    private static final String HOST = "192.168.178.96";
    private static final int PORT = 4223;
    private static IPConnection ipcon = null;
    private static WeatherListener weatherListener = null;
    private FxController controller;

    public static void main(String args[]) {
        ipcon = new IPConnection();

        while(true) {
            try {
                ipcon.connect(HOST, PORT);
                break;
            } catch(com.tinkerforge.TinkerforgeException e) {
            }

            try {
                Thread.sleep(1000);
            } catch(InterruptedException ei) {
            }
        }

        weatherListener = new WeatherListener(ipcon);
        ipcon.addEnumerateListener(weatherListener);
        ipcon.addConnectedListener(weatherListener);

        while(true) {
            try {
                ipcon.enumerate();
                break;
            } catch(com.tinkerforge.NotConnectedException e) {
            }

            try {
                Thread.sleep(1000);
            } catch(InterruptedException ei) {
            }
        }

        System.out.println("Wetter vong Heute");
        launch(args);


    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui.fxml"));
        Parent parent = loader.load();
        controller = loader.getController();

        primaryStage.setTitle("Wetter vong Heute");
        primaryStage.setScene(new Scene(parent));
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        controller.saveProperties();
    }
}

