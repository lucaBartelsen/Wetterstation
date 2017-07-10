package de.swm.wetterstation.test;

import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.BorderPane;
import javafx.scene.shape.Rectangle;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by luca on 07.07.2017.
 */
public class FxController {

    @FXML
    private BorderPane borderPane;
    private JDBC jdbc;
    private NumberAxis temperaturX;
    private NumberAxis temperaturY;
    private LineChart<Number, Number> temperaturChart;
    private XYChart.Series<Number, Number> temperaturSeries;

    @FXML
    private void initialize() {
        try {
            jdbc = new JDBC();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        temperaturX = new NumberAxis(0, 24, 1); // (xMin, xMax, ticks)
        temperaturY = new NumberAxis(jdbc.getLowTemperatur24()-5, jdbc.getHighTemperatur24()+5, 5);  // (yMin, yMax, ticks)
        temperaturX.setLabel("Uhrzeit");
        temperaturY.setLabel("Temperatur");
        temperaturChart = new LineChart<>(temperaturX, temperaturY);
        temperaturSeries = new XYChart.Series<>();
        temperaturSeries.setName("Temperatur");
        temperaturChart.getData().addAll(temperaturSeries);
        temperaturChart.setCreateSymbols(false);
        ArrayList<Double> werte = null;
        ArrayList<Long> zeit = null;
        int n  = 0;
        for (ArrayList a: jdbc.getTemperaturArray()) {
            n++;
            if(n==1) {
                zeit = a;
            }else{
                werte = a;
            }
        }

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
            temperaturSeries.getData().add((new XYChart.Data<>(hmin, d)));
            i++;
        }
        borderPane.setCenter(temperaturChart);
    }

    public void updateChart() {
    }
}
