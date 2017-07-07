package de.swm.wetterstation.test;

import org.postgresql.ds.PGSimpleDataSource;

import java.sql.*;
import java.util.Date;
import java.util.WeakHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Created by luca on 05.07.2017.
 */
public class JDBC {

    private Connection connection;
    private WeatherListener weatherListener;

    public JDBC(WeatherListener weatherListener) throws SQLException {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setDatabaseName("Wetterstation");
        dataSource.setServerName("localhost");
        dataSource.setPortNumber(5432);
        dataSource.setUser("postgres");
        dataSource.setPassword("Pa$$w0rd");
        connection = dataSource.getConnection();
        this.weatherListener = weatherListener;

    }

    public void updateQuery(double value, String type) throws SQLException {
        int exec;
        do {
            String updateQuery = "update public.\"Wetter\" set " + type + " = ? where zeitstempel = ?";
            PreparedStatement update = connection.prepareStatement(updateQuery);
            update.setDouble(1,value);
            update.setLong(2,TimeUnit.MILLISECONDS.toMinutes(new Date().getTime()));
            exec = update.executeUpdate();
            if (exec == 0) {
                String insertQuery = "insert into public.\"Wetter\" (zeitstempel) values (?)";
                PreparedStatement insert = connection.prepareStatement(insertQuery);
                insert.setLong(1,TimeUnit.MILLISECONDS.toMinutes(new Date().getTime()));
                insert.executeUpdate();
            }
        }while (exec == 0);
        weatherListener.display(type,value);
    }

    public double getHelligkeit() {
        String readQuery = "select helligkeit from \"Wetter\" where zeitstempel = ?";
        double helligkeit = 0.0;
        try {
        PreparedStatement read = connection.prepareStatement(readQuery);

            read.setLong(1,TimeUnit.MILLISECONDS.toMinutes(new Date().getTime())-1);
        ResultSet result = read.executeQuery();
        while (result.next()){
            helligkeit = result.getDouble(1);
        }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return helligkeit;
    }

    public void getLuftdruck() throws SQLException {
        String readQuery = "select * from Luftdruck";
        PreparedStatement read = connection.prepareStatement(readQuery);
        ResultSet result = read.executeQuery();
        while (result.next()){
            double luftdruck = result.getDouble(3);
        }
    }

    public void getLuftfeuchtigkeit() throws SQLException {
        String readQuery = "select * from Luftfeuchtigkeit";
        PreparedStatement read = connection.prepareStatement(readQuery);
        ResultSet result = read.executeQuery();
        while (result.next()){
            double luftfeuchtigkeit = result.getDouble(3);
        }
    }

    public double getTemperatur() {
        String readQuery = "select temperatur from \"Wetter\" where zeitstempel = ?";
        double helligkeit = 0.0;
        try {
            PreparedStatement read = connection.prepareStatement(readQuery);

            read.setLong(1,TimeUnit.MILLISECONDS.toMinutes(new Date().getTime())-1);
            ResultSet result = read.executeQuery();
            while (result.next()){
                helligkeit = result.getDouble(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return helligkeit;
    }
}
