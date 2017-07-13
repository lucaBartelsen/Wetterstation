package de.swm.wetterstation.client;

import com.csvreader.CsvWriter;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Array;
import java.sql.SQLException;
import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static java.lang.String.format;

/**
 * Created by luca on 07.07.2017.
 */
public class FxController {

    public Button drucken;
    @FXML
    private Spinner<Integer> slider;
    @FXML
    private Label einheitschwellwertkalt;
    @FXML
    private Label einheitschwellwertwarm;
    @FXML
    private TextField schwellwertKalt;
    @FXML
    private TextField schwellwertWarm;
    @FXML
    private ToggleGroup knopf;
    @FXML
    private RadioButton celsius;
    @FXML
    private RadioButton fahrenheit;
    @FXML
    private Label einheit;
    @FXML
    private Label übersicht_temperatur;
    @FXML
    private Label übersicht_einheit;
    @FXML
    private Label gemessenZeit;
    @FXML
    private Label übersicht_helligkeit;
    @FXML
    private Label übersicht_luftfeuchtigkeit;
    @FXML
    private Label übersicht_luftdruck;
    @FXML
    private Label helligkeitLabel;
    @FXML
    private Label luftdruckLabel;
    @FXML
    private Label luftfeuchtigkeitLabel;
    @FXML
    private Label temperaturLabel;
    @FXML
    private BorderPane temperaturPane;
    @FXML
    private BorderPane luftdruckPane;
    @FXML
    private BorderPane luftfeuchtigkeitPane;
    @FXML
    private BorderPane helligkeitPane;
    private JDBC jdbc;
    private NumberAxis temperaturX;
    private NumberAxis temperaturY;
    private LineChart<Number, Number> temperaturChart;
    private XYChart.Series<Number, Number> temperaturSeries;
    private NumberAxis luftdruckY;
    private NumberAxis luftdruckX;
    private LineChart<Number, Number> luftdruckChart;
    private XYChart.Series<Number, Number> luftdruckSeries;
    private NumberAxis luftfeuchtigkeitX;
    private NumberAxis luftfeuchtigkeitY;
    private LineChart<Number, Number> luftfeuchtigkeitChart;
    private XYChart.Series<Number, Number> luftfeuchtigkeitSeries;
    private NumberAxis helligkeitX;
    private NumberAxis helligkeitY;
    private LineChart<Number, Number> helligkeitChart;
    private XYChart.Series<Number, Number> helligkeitSeries;
    private Timeline timeline = null;
    private Properties properties = null;
    private Path path = null;
    private long today = 0;
    private long tempDay = 0;
    private long luftdDay = 0;
    private long luftfDay = 0;
    private long hellDay = 0;

    @FXML
    private void initialize() {
        today = TimeUnit.MILLISECONDS.toDays(new Date().getTime());
        System.out.println(today);
        tempDay = today;
        luftdDay = today;
        luftfDay = today;
        hellDay = today;
        try {
            jdbc = new JDBC();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        path = Paths.get(System.getProperty("user.home"), "config.properties");
        InputStream is = null;
        properties = new Properties();
        try {
            is = Files.newInputStream(path);
            properties.load(is);
            slider.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 20, (int) Double.parseDouble(String.valueOf(properties.get("Aktual")))));
            if (properties.get("Einheit").equals("fahrenheit")) {
                fahrenheit.setSelected(true);
                celsius.setSelected(false);
            } else {
                if (properties.get("Einheit").equals("celsius")) {
                    celsius.setSelected(true);
                    fahrenheit.setSelected(false);
                }
            }
            schwellwertWarm.setText(String.valueOf(properties.get("SchwellWarm")));
            schwellwertKalt.setText(String.valueOf(properties.get("SchwellKalt")));
        } catch (IOException e) {
            slider.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 20, 5));
            System.err.println("Datei nicht Gefunden");
        }
        ArrayList<Double> temperatur = null;
        ArrayList<Double> luftdruck = null;
        ArrayList<Double> luftfeuchtigkeit = null;
        ArrayList<Double> helligkeit = null;
        ArrayList<Long> zeit = null;
        int n = 0;
        for (ArrayList a : jdbc.getWetterdatenArray()) {
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
        initializeCharts();
        temperaturChart(temperatur, zeit);
        luftdruckChart(luftdruck, zeit);
        luftfeuchtigkeitChart(luftfeuchtigkeit, zeit);
        helligkeitChart(helligkeit, zeit);
        temperaturPane.setCenter(temperaturChart);
        luftdruckPane.setCenter(luftdruckChart);
        luftfeuchtigkeitPane.setCenter(luftfeuchtigkeitChart);
        helligkeitPane.setCenter(helligkeitChart);
        SimpleDateFormat zeitFormat = new SimpleDateFormat("HH:mm");
        gemessenZeit.setText(zeitFormat.format(new Date()));
        fahrenheit.setUserData("fahrenheit");
        celsius.setUserData("celsius");
        if (celsius.isSelected()) {
            einheit.setText("°C");
            übersicht_einheit.setText("°C");
            einheitschwellwertkalt.setText("°C");
            einheitschwellwertwarm.setText("°C");
        } else {
            if (fahrenheit.isSelected()) {
                einheit.setText("°F");
                übersicht_einheit.setText("°F");
                einheitschwellwertkalt.setText("°F");
                einheitschwellwertwarm.setText("°F");
            }
        }
        knopf.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            switch (newValue.getUserData().toString()) {
                case "celsius": {
                    einheit.setText("°C");
                    übersicht_einheit.setText("°C");
                    einheitschwellwertkalt.setText("°C");
                    einheitschwellwertwarm.setText("°C");
                    double temp = (Double.parseDouble(übersicht_temperatur.getText().replace(",", ".")) - 32) * 5 / 9;
                    übersicht_temperatur.setText(format("%.1f", temp));
                    temperaturLabel.setText(format("%.1f", temp));
                    schwellwertKalt.setText(format("%.1f", (Double.parseDouble(schwellwertKalt.getText().replace(",", ".")) - 32) * 5 / 9));
                    schwellwertWarm.setText(format("%.1f", (Double.parseDouble(schwellwertWarm.getText().replace(",", ".")) - 32) * 5 / 9));
                    break;
                }
                case "fahrenheit": {
                    einheit.setText("°F");
                    übersicht_einheit.setText("°F");
                    einheitschwellwertkalt.setText("°F");
                    einheitschwellwertwarm.setText("°F");
                    double temp = Double.parseDouble(übersicht_temperatur.getText().replace(",", ".")) * 1.8 + 32;
                    übersicht_temperatur.setText(format("%.1f", temp));
                    temperaturLabel.setText(format("%.1f", temp));
                    schwellwertKalt.setText(format("%.1f", Double.parseDouble(schwellwertKalt.getText().replace(",", ".")) * 1.8 + 32));
                    schwellwertWarm.setText(format("%.1f", Double.parseDouble(schwellwertWarm.getText().replace(",", ".")) * 1.8 + 32));
                    break;
                }
                default:
            }
        });
        timeline = new Timeline(new KeyFrame(Duration.minutes(slider.getValue()), event -> {
            newDay();
            updateTempChart();
            updateHellChart();
            updateLuftFChart();
            updateLuftDChart();
            gemessenZeit.setText(zeitFormat.format(new Date()));
            if (Double.parseDouble(übersicht_temperatur.getText().replace(",", ".")) > Double.parseDouble(schwellwertWarm.getText().replace(",", "."))) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Hitzewarnung");
                alert.setHeaderText("Es ist zu Heiß");
                alert.setContentText("Die Temperatur ist auf über " + schwellwertWarm.getText() + " " + einheit.getText() + " gestiegen");
                alert.show();
            }
            if (Double.parseDouble(übersicht_temperatur.getText().replace(",", ".")) < Double.parseDouble(schwellwertKalt.getText().replace(",", "."))) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Kältewarnung");
                alert.setHeaderText("Es ist zu Kalt");
                alert.setContentText("Die Temperatur ist auf unter " + schwellwertKalt.getText() + " " + einheit.getText() + " gesunken");
                alert.show();
            }
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
        slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            newTimeline();
        });

        schwellwertKalt.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("([-])?\\d{0,7}([\\,]\\d{0,4})?")) {
                schwellwertKalt.setText(oldValue);
            }
            newTimeline();
        });

        schwellwertWarm.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("([-])?\\d{0,7}([\\,]\\d{0,2})?")) {
                schwellwertWarm.setText(oldValue);
            }
            newTimeline();
        });
    }

    public void updateTempChart() {

        Task<Double> task = new Task<Double>() {
            @Override
            protected Double call() throws Exception {
                double temp = jdbc.getTemperatur();
                return temp;
            }
        };


        task.setOnSucceeded(event -> {
            double temp = task.getValue();
            SimpleDateFormat forHour = new SimpleDateFormat("HH");
            SimpleDateFormat forMin = new SimpleDateFormat("mm");
            Date date = new Date(new Date().getTime());
            int hours = Integer.parseInt(forHour.format(date));
            int minutes = Integer.parseInt(forMin.format(date));
            double hmin = hours + (minutes / 60.0);


            temperaturY.setLowerBound((int) jdbc.getLowTemperatur24() - 5);
            temperaturY.setUpperBound((int) jdbc.getHighTemperatur24() + 5);
            temperaturSeries.getData().add((new XYChart.Data<>(hmin, temp)));
            if (fahrenheit.isSelected()) {
                temp = temp * 1.8 + 32;
            }
            temperaturLabel.setText(format("%.1f", temp));
            übersicht_temperatur.setText(format("%.1f", temp));
        });

        new Thread(task).start();
    }

    public void updateLuftDChart() {

        Task<Double> task = new Task<Double>() {
            @Override
            protected Double call() throws Exception {
                double temp = jdbc.getLuftdruck();
                return temp;
            }
        };
        task.setOnSucceeded(event -> {
            double temp = task.getValue();
            SimpleDateFormat forHour = new SimpleDateFormat("HH");
            SimpleDateFormat forMin = new SimpleDateFormat("mm");
            Date date = new Date(new Date().getTime());
            int hours = Integer.parseInt(forHour.format(date));
            int minutes = Integer.parseInt(forMin.format(date));
            double hmin = hours + (minutes / 60.0);

            luftdruckY.setLowerBound((int) jdbc.getLowLuftdruck24() - 15);
            luftdruckY.setUpperBound((int) jdbc.getHighLuftdruck24() + 15);
            luftdruckSeries.getData().add((new XYChart.Data<>(hmin, temp)));
            luftdruckLabel.setText(format("%.0f", temp));
            übersicht_luftdruck.setText(format("%.0f", temp));
        });

        new Thread(task).start();
    }

    public void updateLuftFChart() {

        Task<Double> task = new Task<Double>() {
            @Override
            protected Double call() throws Exception {
                double temp = jdbc.getLuftfeuchtigkeit();
                return temp;
            }
        };
        task.setOnSucceeded(event -> {
            double temp = task.getValue();
            SimpleDateFormat forHour = new SimpleDateFormat("HH");
            SimpleDateFormat forMin = new SimpleDateFormat("mm");
            Date date = new Date(new Date().getTime());
            int hours = Integer.parseInt(forHour.format(date));
            int minutes = Integer.parseInt(forMin.format(date));
            double hmin = hours + (minutes / 60.0);

            int lowLuft = (int) jdbc.getLowLuftfeucht24();
            int highLuft = (int) jdbc.getHighLuftfeucht24();
            if ((lowLuft - 5) < 0) lowLuft = 0;
            else lowLuft = lowLuft - 5;
            if ((highLuft + 5) > 100) highLuft = 100;
            else highLuft = highLuft + 5;
            luftfeuchtigkeitY.setLowerBound(lowLuft);
            luftfeuchtigkeitY.setUpperBound(highLuft);
            luftfeuchtigkeitSeries.getData().add((new XYChart.Data<>(hmin, temp)));
            luftfeuchtigkeitLabel.setText(format("%.1f", temp));
            übersicht_luftfeuchtigkeit.setText(format("%.1f", temp));
        });

        new Thread(task).start();
    }

    public void updateHellChart() {

        Task<Double> task = new Task<Double>() {
            @Override
            protected Double call() throws Exception {
                double temp = jdbc.getHelligkeit();
                return temp;
            }
        };
        task.setOnSucceeded(event -> {
            double temp = task.getValue();
            SimpleDateFormat forHour = new SimpleDateFormat("HH");
            SimpleDateFormat forMin = new SimpleDateFormat("mm");
            Date date = new Date(new Date().getTime());
            int hours = Integer.parseInt(forHour.format(date));
            int minutes = Integer.parseInt(forMin.format(date));
            double hmin = hours + (minutes / 60.0);

            int lowHell = (int) jdbc.getLowHelligkeit24();
            if ((lowHell - 100) < 0) lowHell = 0;
            else lowHell = lowHell - 100;
            helligkeitY.setLowerBound(lowHell);
            helligkeitY.setUpperBound((int) jdbc.getHighHelligkeit24() + 100);
            helligkeitSeries.getData().add((new XYChart.Data<>(hmin, temp)));
            helligkeitLabel.setText(format("%.1f", temp));
            übersicht_helligkeit.setText(format("%.1f", temp));
        });

        new Thread(task).start();
    }

    public void updateTemp() {

        Task<Double> task = new Task<Double>() {
            @Override
            protected Double call() throws Exception {
                double temp = jdbc.getTemperatur();
                return temp;
            }
        };


        task.setOnSucceeded(event -> {
            double temp = task.getValue();
            if (fahrenheit.isSelected()) {
                temp = temp * 1.8 + 32;
            }
            temperaturLabel.setText(format("%.1f", temp));
            übersicht_temperatur.setText(format("%.1f", temp));
        });

        new Thread(task).start();
    }

    public void updateLuftD() {

        Task<Double> task = new Task<Double>() {
            @Override
            protected Double call() throws Exception {
                double temp = jdbc.getLuftdruck();
                return temp;
            }
        };
        task.setOnSucceeded(event -> {
            double temp = task.getValue();
            luftdruckLabel.setText(format("%.0f", temp));
            übersicht_luftdruck.setText(format("%.0f", temp));
        });

        new Thread(task).start();
    }

    public void updateLuftF() {

        Task<Double> task = new Task<Double>() {
            @Override
            protected Double call() throws Exception {
                double temp = jdbc.getLuftfeuchtigkeit();
                return temp;
            }
        };
        task.setOnSucceeded(event -> {
            double temp = task.getValue();
            luftfeuchtigkeitLabel.setText(format("%.1f", temp));
            übersicht_luftfeuchtigkeit.setText(format("%.1f", temp));
        });

        new Thread(task).start();
    }

    public void updateHell() {

        Task<Double> task = new Task<Double>() {
            @Override
            protected Double call() throws Exception {
                double temp = jdbc.getHelligkeit();
                return temp;
            }
        };
        task.setOnSucceeded(event -> {
            double temp = task.getValue();
            helligkeitLabel.setText(format("%.1f", temp));
            übersicht_helligkeit.setText(format("%.1f", temp));
        });

        new Thread(task).start();
    }

    public void initializeCharts(){
        temperaturX = new NumberAxis(0, 24, 1); // (xMin, xMax, ticks)
        temperaturY = new NumberAxis((int) jdbc.getLowTemperatur24() - 5, (int) jdbc.getHighTemperatur24() + 5, 2);  // (yMin, yMax, ticks)
        temperaturX.setLabel("Uhrzeit");
        temperaturY.setLabel("Temperatur in °C");
        temperaturChart = new LineChart<>(temperaturX, temperaturY);
        temperaturSeries = new XYChart.Series<>();
        temperaturSeries.setName("Temperatur");
        temperaturChart.getData().addAll(temperaturSeries);
        temperaturChart.setCreateSymbols(false);
        temperaturChart.setLegendVisible(false);

        luftdruckX = new NumberAxis(0, 24, 1); // (xMin, xMax, ticks)
        luftdruckY = new NumberAxis(jdbc.getLowLuftdruck24() - 15, jdbc.getHighLuftdruck24() + 15, 5);  // (yMin, yMax, ticks)
        luftdruckX.setLabel("Uhrzeit");
        luftdruckY.setLabel("Luftdruck in mBar");
        luftdruckChart = new LineChart<>(luftdruckX, luftdruckY);
        luftdruckSeries = new XYChart.Series<>();
        luftdruckSeries.setName("Luftdruck");
        luftdruckChart.getData().addAll(luftdruckSeries);
        luftdruckChart.setCreateSymbols(false);
        luftdruckChart.setLegendVisible(false);

        luftfeuchtigkeitX = new NumberAxis(0, 24, 1); // (xMin, xMax, ticks)
        int lowLuft = (int) jdbc.getLowLuftfeucht24();
        int highLuft = (int) jdbc.getHighLuftfeucht24();
        if ((lowLuft - 5) < 0) lowLuft = 0;
        else lowLuft = lowLuft - 5;
        if ((highLuft + 5) > 100) highLuft = 100;
        else highLuft = highLuft + 5;
        luftfeuchtigkeitY = new NumberAxis(lowLuft, highLuft, 5);  // (yMin, yMax, ticks)
        luftfeuchtigkeitX.setLabel("Uhrzeit");
        luftfeuchtigkeitY.setLabel("Luftfeuchtigkeit in %");
        luftfeuchtigkeitChart = new LineChart<>(luftfeuchtigkeitX, luftfeuchtigkeitY);
        luftfeuchtigkeitSeries = new XYChart.Series<>();
        luftfeuchtigkeitSeries.setName("Luftfeuchtigkeit");
        luftfeuchtigkeitChart.getData().addAll(luftfeuchtigkeitSeries);
        luftfeuchtigkeitChart.setCreateSymbols(false);
        luftfeuchtigkeitChart.setLegendVisible(false);

        helligkeitX = new NumberAxis(0, 24, 1); // (xMin, xMax, ticks)
        int lowHell = (int) jdbc.getLowHelligkeit24();
        if ((lowHell - 100) < 0) lowHell = 0;
        else lowHell = lowHell - 100;
        helligkeitY = new NumberAxis(lowHell, jdbc.getHighHelligkeit24() + 100, 100);  // (yMin, yMax, ticks)
        helligkeitX.setLabel("Uhrzeit");
        helligkeitY.setLabel("Helligkeit in Lx");
        helligkeitChart = new LineChart<>(helligkeitX, helligkeitY);
        helligkeitSeries = new XYChart.Series<>();
        helligkeitSeries.setName("Helligkeit");
        helligkeitChart.getData().addAll(helligkeitSeries);
        helligkeitChart.setCreateSymbols(false);
        helligkeitChart.setLegendVisible(false);
    }
    public void temperaturChart(ArrayList<Double> werte, ArrayList<Long> zeit) {

        SimpleDateFormat forHour = new SimpleDateFormat("HH");
        SimpleDateFormat forMin = new SimpleDateFormat("mm");

        int i = 0;
        for (double d : werte) {
            long time = TimeUnit.MINUTES.toMillis(zeit.get(i));
            System.out.println(time + " : " + Instant.ofEpochMilli(time));
            ZonedDateTime t1 = ZonedDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault());

            Date date = new Date(time);
            int hours = t1.getHour();
            int minutes = t1.getMinute();

            System.out.println("H: " + hours + " M: " + minutes);

            double hmin = hours + (minutes / 60.0);
            System.out.println(hmin);
            temperaturSeries.getData().add((new XYChart.Data<>(hmin, d)));
            double temp = d;
            if (fahrenheit.isSelected()) {
                temp = temp * 1.8 + 32;
            }
            temperaturLabel.setText(format("%.1f", temp));
            übersicht_temperatur.setText(format("%.1f", temp));
            i++;
        }
        System.out.println(zeit.get(0));
        System.out.println(zeit.get(zeit.size()-1));
    }

    public void luftdruckChart(ArrayList<Double> werte, ArrayList<Long> zeit) {
        int i = 0;
        for (double d : werte) {
            SimpleDateFormat forHour = new SimpleDateFormat("HH");
            SimpleDateFormat forMin = new SimpleDateFormat("mm");
            long time = zeit.get(i) * 60000;
            Date hell = new Date(time);
            int hours = Integer.parseInt(forHour.format(hell));
            int minutes = Integer.parseInt(forMin.format(hell));
            double hmin = hours + (minutes / 60.0);
            luftdruckSeries.getData().add((new XYChart.Data<>(hmin, d)));
            luftdruckLabel.setText(format("%.1f", d));
            übersicht_luftdruck.setText(format("%.1f", d));
            i++;
        }
    }

    public void luftfeuchtigkeitChart(ArrayList<Double> werte, ArrayList<Long> zeit) {
        int i = 0;
        for (double d : werte) {
            SimpleDateFormat forHour = new SimpleDateFormat("HH");
            SimpleDateFormat forMin = new SimpleDateFormat("mm");
            long time = zeit.get(i) * 60000;
            Date hell = new Date(time);
            int hours = Integer.parseInt(forHour.format(hell));
            int minutes = Integer.parseInt(forMin.format(hell));
            double hmin = hours + (minutes / 60.0);
            luftfeuchtigkeitSeries.getData().add((new XYChart.Data<>(hmin, d)));
            luftfeuchtigkeitLabel.setText(format("%.1f", d));
            übersicht_luftfeuchtigkeit.setText(format("%.1f", d));
            i++;
        }
    }

    public void helligkeitChart(ArrayList<Double> werte, ArrayList<Long> zeit) {
        int i = 0;
        for (double d : werte) {
            SimpleDateFormat forHour = new SimpleDateFormat("HH");
            SimpleDateFormat forMin = new SimpleDateFormat("mm");
            long time = zeit.get(i) * 60000;
            Date hell = new Date(time);
            int hours = Integer.parseInt(forHour.format(hell));
            int minutes = Integer.parseInt(forMin.format(hell));
            double hmin = hours + (minutes / 60.0);
            helligkeitSeries.getData().add((new XYChart.Data<>(hmin, d)));
            helligkeitLabel.setText(format("%.1f", d));
            übersicht_helligkeit.setText(format("%.1f", d));
            i++;
        }
    }

    public void saveProperties() {
        properties.setProperty("Aktual", String.valueOf(slider.getValue()));
        if (celsius.isSelected()) {
            properties.setProperty("Einheit", "celsius");
        } else {
            if (fahrenheit.isSelected()) {
                properties.setProperty("Einheit", "fahrenheit");
            }
        }
        properties.setProperty("SchwellKalt", schwellwertKalt.getText());
        properties.setProperty("SchwellWarm", schwellwertWarm.getText());
        try {
            properties.store(Files.newOutputStream(path), new Date().toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void newDay() {
        long timestampDay = TimeUnit.MILLISECONDS.toDays(new Date().getTime());
        if (timestampDay > today) {
            temperaturSeries.getData().clear();
            helligkeitSeries.getData().clear();
            luftdruckSeries.getData().clear();
            luftfeuchtigkeitSeries.getData().clear();
            today = timestampDay;
        }
    }

    public void druckenPress(ActionEvent actionEvent) {

        Stage stage = new Stage();
        Parent parent = null;
        try {
            parent = FXMLLoader.load(getClass().getResource("/drucken.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Scene scene = new Scene(parent);
        stage.setScene(scene);
        stage.setTitle("Exportieren");
        stage.show();

    }

    public void temperaturBack(ActionEvent actionEvent) {
        temperaturSeries.getData().clear();
        timelineNoChart();
        temperaturChart(jdbc.Temperatur(tempDay-1,tempDay),jdbc.Zeit(tempDay-1,tempDay));
        tempDay = tempDay - 1;
    }

    public void temperaturVor(ActionEvent actionEvent) {
        temperaturSeries.getData().clear();
        timelineNoChart();
        tempDay = tempDay + 1;
        temperaturChart(jdbc.Temperatur(tempDay,tempDay+1),jdbc.Zeit(tempDay,tempDay+1));
    }

    public void luftdruckBack(ActionEvent actionEvent) {
        luftdruckSeries.getData().clear();
        timelineNoChart();
        luftdruckChart(jdbc.Luftdruck(luftdDay-1,luftdDay),jdbc.Zeit(luftdDay-1,luftdDay));
        luftdDay = luftdDay - 1;
    }

    public void luftdruckVor(ActionEvent actionEvent) {
        luftdruckSeries.getData().clear();
        timelineNoChart();
        luftdDay = luftdDay + 1;
        luftdruckChart(jdbc.Luftdruck(luftdDay,luftdDay+1),jdbc.Zeit(luftdDay,luftdDay+1));
    }

    public void luftfeuchtigkeitBack(ActionEvent actionEvent) {
        luftfeuchtigkeitSeries.getData().clear();
        timelineNoChart();
        luftfeuchtigkeitChart(jdbc.Luftfeuchtigkeit(luftfDay-1,luftfDay),jdbc.Zeit(luftfDay-1,luftfDay));
        luftfDay = luftfDay - 1;
    }

    public void luftfeuchtigkeitVor(ActionEvent actionEvent) {
        luftfeuchtigkeitSeries.getData().clear();
        timelineNoChart();
        luftfDay = luftfDay + 1;
        luftdruckChart(jdbc.Luftdruck(luftfDay,luftfDay+1),jdbc.Zeit(luftfDay,luftfDay+1));
    }

    public void helligkeitBack(ActionEvent actionEvent) {
        helligkeitSeries.getData().clear();
        timelineNoChart();
        helligkeitChart(jdbc.Luftfeuchtigkeit(hellDay-1,hellDay),jdbc.Zeit(hellDay-1,hellDay));
        hellDay = hellDay - 1;
    }

    public void helligkeitVor(ActionEvent actionEvent) {
        helligkeitSeries.getData().clear();
        timelineNoChart();
        hellDay = hellDay + 1;
        helligkeitChart(jdbc.Luftfeuchtigkeit(hellDay,hellDay+1),jdbc.Zeit(hellDay,hellDay+1));
    }

    public void newTimeline(){
        SimpleDateFormat zeitFormat = new SimpleDateFormat("HH:mm");
        timeline.stop();
        timeline = new Timeline(new KeyFrame(Duration.minutes(slider.getValue()), event -> {
            newDay();
            updateTempChart();
            updateHellChart();
            updateLuftFChart();
            updateLuftDChart();
            gemessenZeit.setText(zeitFormat.format(new Date()));
            if (Double.parseDouble(übersicht_temperatur.getText().replace(",", ".")) > Double.parseDouble(schwellwertWarm.getText().replace(",", "."))) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Hitzewarnung");
                alert.setHeaderText("Es ist zu Heiß");
                alert.setContentText("Die Temperatur ist auf über " + schwellwertWarm.getText() + " " + einheit.getText() + " gestiegen");
                alert.show();
            }
            if (Double.parseDouble(übersicht_temperatur.getText().replace(",", ".")) < Double.parseDouble(schwellwertKalt.getText().replace(",", "."))) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Kältewarnung");
                alert.setHeaderText("Es ist zu Kalt");
                alert.setContentText("Die Temperatur ist auf unter " + schwellwertKalt.getText() + " " + einheit.getText() + " gesunken");
                alert.show();
            }
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    public void timelineNoChart(){
        SimpleDateFormat zeitFormat = new SimpleDateFormat("HH:mm");
        timeline.stop();
        updateTemp();
        updateHell();
        updateLuftD();
        updateLuftF();
        timeline = new Timeline(new KeyFrame(Duration.minutes(slider.getValue()), event -> {
            gemessenZeit.setText(zeitFormat.format(new Date()));
            if (Double.parseDouble(übersicht_temperatur.getText().replace(",", ".")) > Double.parseDouble(schwellwertWarm.getText().replace(",", "."))) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Hitzewarnung");
                alert.setHeaderText("Es ist zu Heiß");
                alert.setContentText("Die Temperatur ist auf über " + schwellwertWarm.getText() + " " + einheit.getText() + " gestiegen");
                alert.show();
            }
            if (Double.parseDouble(übersicht_temperatur.getText().replace(",", ".")) < Double.parseDouble(schwellwertKalt.getText().replace(",", "."))) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Kältewarnung");
                alert.setHeaderText("Es ist zu Kalt");
                alert.setContentText("Die Temperatur ist auf unter " + schwellwertKalt.getText() + " " + einheit.getText() + " gesunken");
                alert.show();
            }
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }
}
