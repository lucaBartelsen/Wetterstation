package de.swm.wetterstation.client;

import com.csvreader.CsvWriter;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/**
 * Created by luca on 13.07.2017.
 */
public class exportController {

    @FXML
    private Pane pane;
    @FXML
    private Label dateipfad;
    @FXML
    private Button export;
    @FXML
    private Button dateiSucher;
    @FXML
    private DatePicker vonDatum;
    @FXML
    private DatePicker bisDatum;
    private JDBC jdbc = null;
    private File file = null;

    @FXML
    private void initialize(){
        try {
            jdbc = new JDBC();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void exportPress(ActionEvent actionEvent) {


        if(file != null && vonDatum.getValue() != null && bisDatum.getValue() != null){

        String outputFile = file.toString();

        ArrayList<Double> temperatur = null;
        ArrayList<Double> luftdruck = null;
        ArrayList<Double> luftfeuchtigkeit = null;
        ArrayList<Double> helligkeit = null;
        ArrayList<Long> zeit = null;
        int n = 0;
        for (ArrayList a : jdbc.Wetterdaten(vonDatum.getValue().toEpochDay(),bisDatum.getValue().toEpochDay())) {
            n++;
            if (n == 1) {
                zeit = a;
            } else {
                if (n == 2) {
                    temperatur = a;
                } else {
                    if (n == 3) {
                        luftdruck = a;
                    } else {
                        if (n == 4) {
                            luftfeuchtigkeit = a;
                        } else {
                            helligkeit = a;
                        }
                    }
                }
            }

        }

        // before we open the file check to see if it already exists
        boolean alreadyExists = new File(outputFile).exists();

        try {
            // use FileWriter constructor that specifies open for appending
            CsvWriter csvOutput = new CsvWriter(new FileWriter(outputFile, true), ';');

            // if the file didn't already exist then we need to write out the header line
            if (!alreadyExists)
            {
                csvOutput.write("Uhrzeit");
                csvOutput.write("Temperatur");
                csvOutput.write("Luftdruck");
                csvOutput.write("Luftfeuchtigkeit");
                csvOutput.write("Helligkeit");
                csvOutput.endRecord();
            }
            DateFormat formatter = new SimpleDateFormat("dd.MM.yy HH:mm");
            int i = 0;
            for (long l: zeit) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(TimeUnit.MINUTES.toMillis(l));
                csvOutput.write(formatter.format(calendar.getTime()));
                csvOutput.write(temperatur.get(i).toString());
                csvOutput.write(luftdruck.get(i).toString());
                csvOutput.write(luftfeuchtigkeit.get(i).toString());
                csvOutput.write(helligkeit.get(i).toString());
                csvOutput.endRecord();
                i++;
            }

            csvOutput.close();
            Runtime.getRuntime().exec("explorer /e,/select, " + file.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        }else{
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Drastischer Fehler gefunden");
            alert.setHeaderText("Sie haben keine datei oder Daten ausgewählt!");
            alert.setContentText("Bitte wählen sie eine Datei und Daten aus.");
            alert.show();
        }
    }

    public void dateiSucherPress(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();

        //Set extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("CSV Datei (*.csv)", "*.csv");
        String userDirectoryString = System.getProperty("user.home");
        File userDirectory = new File(userDirectoryString);
        fileChooser.setInitialDirectory(userDirectory);
        fileChooser.getExtensionFilters().add(extFilter);

        //Show save file dialog
        file = fileChooser.showSaveDialog(pane.getScene().getWindow());
        if (file != null){
            dateipfad.setText(file.toString());
        }

    }
}
