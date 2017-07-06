package de.swm.wetterstation.test;

import org.postgresql.ds.PGSimpleDataSource;

import java.sql.*;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by luca on 05.07.2017.
 */
public class JDBC {

    private Connection connection;

    public JDBC() throws SQLException {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setDatabaseName("Wetterstation");
        dataSource.setServerName("localhost");
        dataSource.setPortNumber(5432);
        dataSource.setUser("postgres");
        dataSource.setPassword("Pa$$w0rd");
        connection = dataSource.getConnection();

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
    }

    public void getTimestamp() throws SQLException {
        String readQuery = "select * from wetter";
        PreparedStatement read = connection.prepareStatement(readQuery);
        ResultSet result = read.executeQuery();
        while (result.next()){
            Timestamp time = result.getTimestamp(2);
        }
    }

    public void getHelligkeit() throws SQLException {
        String readQuery = "select * from Helligkeit";
        PreparedStatement read = connection.prepareStatement(readQuery);
        ResultSet result = read.executeQuery();
        while (result.next()){
            double helligkeit = result.getDouble(3);
        }
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

    public void getTemperatur() throws SQLException {
        String readQuery = "select * from Temperatur";
        PreparedStatement read = connection.prepareStatement(readQuery);
        ResultSet result = read.executeQuery();
        while (result.next()){
            double temperatur = result.getDouble(3);
        }
    }
}
