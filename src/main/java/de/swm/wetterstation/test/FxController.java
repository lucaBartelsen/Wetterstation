package de.swm.wetterstation.test;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.BorderPane;
import javafx.util.Duration;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by luca on 07.07.2017.
 */
public class FxController {

    @FXML
    private BorderPane temperaturPane;
    @FXML
    private BorderPane luftrduckPane;
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

    @FXML
    private void initialize() {
        try {
            jdbc = new JDBC();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        ArrayList<Double> temperatur = null;
        ArrayList<Double> luftdruck = null;
        ArrayList<Double> luftfeuchtigkeit = null;
        ArrayList<Double> helligkeit = null;
        ArrayList<Long> zeit = null;
        int n = 0;
        for (ArrayList a: jdbc.getWetterdatenArray()) {
            n++;
            if(n==1) {
                zeit = a;
            }else{
                if(n==2){
                    temperatur = a;
                }else{
                    if(n==3){
                        luftdruck = a;
                    }else{
                        if(n==4){
                            luftfeuchtigkeit = a;
                        }else{
                            helligkeit = a;
                        }
                    }
                }
            }
        }
        temperaturChart(temperatur,zeit);
        luftdruckChart(luftdruck,zeit);
        luftfeuchtigkeitChart(luftfeuchtigkeit,zeit);
        helligkeitChart(helligkeit,zeit);
        temperaturPane.setCenter(temperaturChart);
        luftrduckPane.setCenter(luftdruckChart);
        luftfeuchtigkeitPane.setCenter(luftfeuchtigkeitChart);
        helligkeitPane.setCenter(helligkeitChart);


        Timeline timeline = new Timeline(new KeyFrame(Duration.minutes(5), event -> {
            updateTempChart();
            updateHellChart();
            updateLuftFChart();
            updateLuftDChart();
            System.out.println("Update Charts");
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    public void updateTempChart() {
        SimpleDateFormat forHour = new SimpleDateFormat("HH");
        SimpleDateFormat forMin = new SimpleDateFormat("mm");
        Date date = new Date(new Date().getTime()-1);
        int hours = Integer.parseInt(forHour.format(date));
        int minutes = Integer.parseInt(forMin.format(date));
        double hmin = hours + (minutes/60.0);
        temperaturSeries.getData().add((new XYChart.Data<>(hmin, jdbc.getTemperatur())));
    }

    public void updateLuftDChart() {
        SimpleDateFormat forHour = new SimpleDateFormat("HH");
        SimpleDateFormat forMin = new SimpleDateFormat("mm");
        Date date = new Date(new Date().getTime()-1);
        int hours = Integer.parseInt(forHour.format(date));
        int minutes = Integer.parseInt(forMin.format(date));
        double hmin = hours + (minutes/60.0);
        luftdruckSeries.getData().add((new XYChart.Data<>(hmin, jdbc.getLuftdruck())));
    }

    public void updateLuftFChart() {
        SimpleDateFormat forHour = new SimpleDateFormat("HH");
        SimpleDateFormat forMin = new SimpleDateFormat("mm");
        Date date = new Date(new Date().getTime()-1);
        int hours = Integer.parseInt(forHour.format(date));
        int minutes = Integer.parseInt(forMin.format(date));
        double hmin = hours + (minutes/60.0);
        luftfeuchtigkeitSeries.getData().add((new XYChart.Data<>(hmin, jdbc.getLuftfeuchtigkeit())));
    }

    public void updateHellChart() {
        SimpleDateFormat forHour = new SimpleDateFormat("HH");
        SimpleDateFormat forMin = new SimpleDateFormat("mm");
        Date date = new Date(new Date().getTime()-1);
        int hours = Integer.parseInt(forHour.format(date));
        int minutes = Integer.parseInt(forMin.format(date));
        double hmin = hours + (minutes/60.0);
        helligkeitSeries.getData().add((new XYChart.Data<>(hmin, jdbc.getHelligkeit())));
    }

    public void temperaturChart(ArrayList<Double> werte, ArrayList<Long> zeit){
        temperaturX = new NumberAxis(0, 24, 1); // (xMin, xMax, ticks)
        temperaturY = new NumberAxis(jdbc.getLowTemperatur24()-5, jdbc.getHighTemperatur24()+5, 5);  // (yMin, yMax, ticks)
        temperaturX.setLabel("Uhrzeit");
        temperaturY.setLabel("Temperatur");
        temperaturChart = new LineChart<>(temperaturX, temperaturY);
        temperaturSeries = new XYChart.Series<>();
        temperaturSeries.setName("Temperatur");
        temperaturChart.getData().addAll(temperaturSeries);
        temperaturChart.setCreateSymbols(false);
        int i = 0;
        for (double d:werte) {
            SimpleDateFormat forHour = new SimpleDateFormat("HH");
            SimpleDateFormat forMin = new SimpleDateFormat("mm");
            long time = zeit.get(i)*60000;
            Date date = new Date(time);
            int hours = Integer.parseInt(forHour.format(date));
            int minutes = Integer.parseInt(forMin.format(date));
            double hmin = hours + (minutes/60.0);
            temperaturSeries.getData().add((new XYChart.Data<>(hmin, d)));
            i++;
        }
    }

    public void luftdruckChart(ArrayList<Double> werte, ArrayList<Long> zeit){
        luftdruckX = new NumberAxis(0, 24, 1); // (xMin, xMax, ticks)
        luftdruckY = new NumberAxis(500, 2000, 50);  // (yMin, yMax, ticks)
        luftdruckX.setLabel("Uhrzeit");
        luftdruckY.setLabel("Luftdruck");
        luftdruckChart = new LineChart<>(luftdruckX, luftdruckY);
        luftdruckSeries = new XYChart.Series<>();
        luftdruckSeries.setName("Luftdruck");
        luftdruckChart.getData().addAll(luftdruckSeries);
        luftdruckChart.setCreateSymbols(false);
        int i = 0;
        for (double d:werte) {
            SimpleDateFormat forHour = new SimpleDateFormat("HH");
            SimpleDateFormat forMin = new SimpleDateFormat("mm");
            System.out.println(zeit.get(i));
            long time = zeit.get(i)*60000;
            Date hell = new Date(time);
            int hours = Integer.parseInt(forHour.format(hell));
            int minutes = Integer.parseInt(forMin.format(hell));
            double hmin = hours + (minutes/60.0);
            luftdruckSeries.getData().add((new XYChart.Data<>(hmin, d)));
            i++;
        }
    }

    public void luftfeuchtigkeitChart(ArrayList<Double> werte, ArrayList<Long> zeit){
        luftfeuchtigkeitX = new NumberAxis(0, 24, 1); // (xMin, xMax, ticks)
        luftfeuchtigkeitY = new NumberAxis(0, 100, 10);  // (yMin, yMax, ticks)
        luftfeuchtigkeitX.setLabel("Uhrzeit");
        luftfeuchtigkeitY.setLabel("Luftfeuchtigkeit");
        luftfeuchtigkeitChart = new LineChart<>(luftfeuchtigkeitX, luftfeuchtigkeitY);
        luftfeuchtigkeitSeries = new XYChart.Series<>();
        luftfeuchtigkeitSeries.setName("Luftfeuchtigkeit");
        luftfeuchtigkeitChart.getData().addAll(luftfeuchtigkeitSeries);
        luftfeuchtigkeitChart.setCreateSymbols(false);
        int i = 0;
        for (double d:werte) {
            SimpleDateFormat forHour = new SimpleDateFormat("HH");
            SimpleDateFormat forMin = new SimpleDateFormat("mm");
            System.out.println(zeit.get(i));
            long time = zeit.get(i)*60000;
            Date hell = new Date(time);
            int hours = Integer.parseInt(forHour.format(hell));
            int minutes = Integer.parseInt(forMin.format(hell));
            double hmin = hours + (minutes/60.0);
            luftfeuchtigkeitSeries.getData().add((new XYChart.Data<>(hmin, d)));
            i++;
        }
    }

    public void helligkeitChart(ArrayList<Double> werte, ArrayList<Long> zeit){
        helligkeitX = new NumberAxis(0, 24, 1); // (xMin, xMax, ticks)
        helligkeitY = new NumberAxis(0, 3000, 50);  // (yMin, yMax, ticks)
        helligkeitX.setLabel("Uhrzeit");
        helligkeitY.setLabel("Temperatur");
        helligkeitChart = new LineChart<>(helligkeitX, helligkeitY);
        helligkeitSeries = new XYChart.Series<>();
        helligkeitSeries.setName("Temperatur");
        helligkeitChart.getData().addAll(helligkeitSeries);
        helligkeitChart.setCreateSymbols(false);
        int i = 0;
        for (double d:werte) {
            SimpleDateFormat forHour = new SimpleDateFormat("HH");
            SimpleDateFormat forMin = new SimpleDateFormat("mm");
            System.out.println(zeit.get(i));
            long time = zeit.get(i)*60000;
            Date hell = new Date(time);
            int hours = Integer.parseInt(forHour.format(hell));
            int minutes = Integer.parseInt(forMin.format(hell));
            double hmin = hours + (minutes/60.0);
            helligkeitSeries.getData().add((new XYChart.Data<>(hmin, d)));
            i++;
        }
    }
}
