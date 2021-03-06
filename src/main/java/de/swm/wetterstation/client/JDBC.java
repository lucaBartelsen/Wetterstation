package de.swm.wetterstation.client;

import org.postgresql.ds.PGSimpleDataSource;

import java.sql.*;
import java.time.ZonedDateTime;
import java.util.ArrayList;
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
        dataSource.setServerName("192.168.178.138"); //192.168.178.138
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
            update.setDouble(1, value);
            update.setLong(2, TimeUnit.MILLISECONDS.toMinutes(new Date().getTime()));
            exec = update.executeUpdate();
            if (exec == 0) {
                String insertQuery = "insert into public.\"Wetter\" (zeitstempel) values (?)";
                PreparedStatement insert = connection.prepareStatement(insertQuery);
                insert.setLong(1, TimeUnit.MILLISECONDS.toMinutes(new Date().getTime()));
                insert.executeUpdate();
            }
        } while (exec == 0);
    }

    public double getHelligkeit() {
        String readQuery = "select helligkeit from \"Wetter\" where zeitstempel <= ? AND helligkeit IS NOT NULL order by zeitstempel desc limit 1";
        double helligkeit = 0.0;
            try {
                PreparedStatement read = connection.prepareStatement(readQuery);

                read.setLong(1, TimeUnit.MILLISECONDS.toMinutes(new Date().getTime()));
                ResultSet result = read.executeQuery();
                while (result.next()) {
                    helligkeit = result.getDouble(1);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        return helligkeit;
    }

    public double getLuftdruck() {
        String readQuery = "select luftdruck from \"Wetter\" where zeitstempel <= ? AND luftdruck IS NOT NULL order by zeitstempel desc limit 1";
        double helligkeit = 0.0;
            try {
                PreparedStatement read = connection.prepareStatement(readQuery);

                read.setLong(1, TimeUnit.MILLISECONDS.toMinutes(new Date().getTime()));
                ResultSet result = read.executeQuery();
                while (result.next()) {
                    helligkeit = result.getDouble(1);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        return helligkeit;
    }

    public double getLuftfeuchtigkeit() {
        String readQuery = "select luftfeuchtigkeit from \"Wetter\" where zeitstempel <= ? AND luftfeuchtigkeit IS NOT NULL order by zeitstempel desc limit 1";
        double helligkeit = 0.0;
            try {
                PreparedStatement read = connection.prepareStatement(readQuery);

                read.setLong(1, TimeUnit.MILLISECONDS.toMinutes(new Date().getTime()));
                ResultSet result = read.executeQuery();
                while (result.next()) {
                    helligkeit = result.getDouble(1);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        return helligkeit;
    }

    public Double getTemperatur() {

        String readQuery = "select temperatur from \"Wetter\" where zeitstempel <= ? AND temperatur IS NOT NULL order by zeitstempel desc limit 1";
        double helligkeit = 0.0;
        try {
            PreparedStatement read = connection.prepareStatement(readQuery);

            read.setLong(1, TimeUnit.MILLISECONDS.toMinutes(new Date().getTime()));
            ResultSet result = read.executeQuery();
            while (result.next()) {
                helligkeit = result.getDouble(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return helligkeit;
    }

    public ArrayList[] getWetterdatenArray() {
        String readQuery = "select zeitstempel, temperatur, luftdruck, luftfeuchtigkeit, helligkeit from \"Wetter\" where zeitstempel >= ?";
        ArrayList<Double> temperatur = new ArrayList<>();
        ArrayList<Double> luftdruck = new ArrayList<>();
        ArrayList<Double> luftfeuchtigkeit = new ArrayList<>();
        ArrayList<Double> helligkeit = new ArrayList<>();
        ArrayList<Long> zeitstempel = new ArrayList<>();
        ArrayList[] uebergabe = new ArrayList[5];
        try {
            PreparedStatement read = connection.prepareStatement(readQuery);
            ZonedDateTime dateTime = ZonedDateTime.now();
            ZonedDateTime start = dateTime.withHour(0).withMinute(0).withSecond(0).withNano(0);
            read.setLong(1, TimeUnit.MILLISECONDS.toMinutes(start.toInstant().toEpochMilli()));
            ResultSet result = read.executeQuery();
            double savet = 0;
            double saveld = 0;
            double savelf = 0;
            double saveh = 0;
            while (result.next()) {

                if (result.getDouble(2) == 0) {
                    temperatur.add(savet);
                } else {
                    temperatur.add(result.getDouble(2));
                    savet = result.getDouble(2);
                }

                if (result.getDouble(3) == 0) {
                    luftdruck.add(saveld);
                } else {
                    luftdruck.add(result.getDouble(3));
                    saveld = result.getDouble(3);
                }

                if (result.getDouble(4) == 0) {
                    luftfeuchtigkeit.add(savelf);
                } else {
                    luftfeuchtigkeit.add(result.getDouble(4));
                    savelf = result.getDouble(4);
                }

                if (result.getDouble(5) == 0) {
                    helligkeit.add(saveh);
                } else {
                    helligkeit.add(result.getDouble(5));
                    saveh = result.getDouble(5);
                }
                zeitstempel.add(result.getLong(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        uebergabe[0] = zeitstempel;
        uebergabe[1] = temperatur;
        uebergabe[2] = luftdruck;
        uebergabe[3] = luftfeuchtigkeit;
        uebergabe[4] = helligkeit;
        return uebergabe;
    }

    public double getLowTemperatur24() {
        String readQuery = "select temperatur from \"Wetter\" where zeitstempel > ? AND temperatur IS NOT NULL ORDER BY temperatur ASC limit 1";
        double temperatur = 0.0;
        try {
            PreparedStatement read = connection.prepareStatement(readQuery);

            ZonedDateTime dateTime = ZonedDateTime.now();
            ZonedDateTime start = dateTime.withHour(0).withMinute(0).withSecond(0).withNano(0);
            read.setLong(1, TimeUnit.MILLISECONDS.toMinutes(start.toInstant().toEpochMilli()));
            ResultSet result = read.executeQuery();
            while (result.next()) {
                temperatur = result.getDouble(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return temperatur;
    }

    public double getHighTemperatur24() {
        String readQuery = "select temperatur from \"Wetter\" where zeitstempel > ? AND temperatur IS NOT NULL ORDER BY temperatur DESC limit 1";
        double temperatur = 0.0;
        try {
            PreparedStatement read = connection.prepareStatement(readQuery);

            ZonedDateTime dateTime = ZonedDateTime.now();
            ZonedDateTime start = dateTime.withHour(0).withMinute(0).withSecond(0).withNano(0);
            read.setLong(1, TimeUnit.MILLISECONDS.toMinutes(start.toInstant().toEpochMilli()));
            ResultSet result = read.executeQuery();
            while (result.next()) {
                temperatur = result.getDouble(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return temperatur;
    }

    public double getLowLuftdruck24() {
        String readQuery = "select luftdruck from \"Wetter\" where zeitstempel > ? AND luftdruck IS NOT NULL ORDER BY luftdruck ASC limit 1";
        double temperatur = 0.0;
        try {
            PreparedStatement read = connection.prepareStatement(readQuery);

            ZonedDateTime dateTime = ZonedDateTime.now();
            ZonedDateTime start = dateTime.withHour(0).withMinute(0).withSecond(0).withNano(0);
            read.setLong(1, TimeUnit.MILLISECONDS.toMinutes(start.toInstant().toEpochMilli()));
            ResultSet result = read.executeQuery();
            while (result.next()) {
                temperatur = result.getDouble(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return temperatur;
    }

    public double getHighLuftdruck24() {
        String readQuery = "select luftdruck from \"Wetter\" where zeitstempel > ? AND luftdruck IS NOT NULL ORDER BY luftdruck DESC limit 1";
        double temperatur = 0.0;
        try {
            PreparedStatement read = connection.prepareStatement(readQuery);

            ZonedDateTime dateTime = ZonedDateTime.now();
            ZonedDateTime start = dateTime.withHour(0).withMinute(0).withSecond(0).withNano(0);
            read.setLong(1, TimeUnit.MILLISECONDS.toMinutes(start.toInstant().toEpochMilli()));
            ResultSet result = read.executeQuery();
            while (result.next()) {
                temperatur = result.getDouble(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return temperatur;
    }

    public double getLowLuftfeucht24() {
        String readQuery = "select luftfeuchtigkeit from \"Wetter\" where zeitstempel > ? AND luftfeuchtigkeit IS NOT NULL ORDER BY luftfeuchtigkeit ASC limit 1";
        double temperatur = 0.0;
        try {
            PreparedStatement read = connection.prepareStatement(readQuery);

            ZonedDateTime dateTime = ZonedDateTime.now();
            ZonedDateTime start = dateTime.withHour(0).withMinute(0).withSecond(0).withNano(0);
            read.setLong(1, TimeUnit.MILLISECONDS.toMinutes(start.toInstant().toEpochMilli()));
            ResultSet result = read.executeQuery();
            while (result.next()) {
                temperatur = result.getDouble(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return temperatur;
    }

    public double getHighLuftfeucht24() {
        String readQuery = "select luftfeuchtigkeit from \"Wetter\" where zeitstempel > ? AND luftfeuchtigkeit IS NOT NULL ORDER BY luftfeuchtigkeit DESC limit 1";
        double temperatur = 0.0;
        try {
            PreparedStatement read = connection.prepareStatement(readQuery);

            ZonedDateTime dateTime = ZonedDateTime.now();
            ZonedDateTime start = dateTime.withHour(0).withMinute(0).withSecond(0).withNano(0);
            read.setLong(1, TimeUnit.MILLISECONDS.toMinutes(start.toInstant().toEpochMilli()));
            ResultSet result = read.executeQuery();
            while (result.next()) {
                temperatur = result.getDouble(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return temperatur;
    }

    public double getLowHelligkeit24() {
        String readQuery = "select helligkeit from \"Wetter\" where zeitstempel > ? AND helligkeit IS NOT NULL ORDER BY helligkeit ASC limit 1";
        double temperatur = 0.0;
        try {
            PreparedStatement read = connection.prepareStatement(readQuery);

            ZonedDateTime dateTime = ZonedDateTime.now();
            ZonedDateTime start = dateTime.withHour(0).withMinute(0).withSecond(0).withNano(0);
            read.setLong(1, TimeUnit.MILLISECONDS.toMinutes(start.toInstant().toEpochMilli()));
            ResultSet result = read.executeQuery();
            while (result.next()) {
                temperatur = result.getDouble(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return temperatur;
    }

    public double getHighHelligkeit24() {
        String readQuery = "select helligkeit from \"Wetter\" where zeitstempel > ? AND helligkeit IS NOT NULL ORDER BY helligkeit DESC limit 1";
        double temperatur = 0.0;
        try {
            PreparedStatement read = connection.prepareStatement(readQuery);

            ZonedDateTime dateTime = ZonedDateTime.now();
            ZonedDateTime start = dateTime.withHour(0).withMinute(0).withSecond(0).withNano(0);
            read.setLong(1, TimeUnit.MILLISECONDS.toMinutes(start.toInstant().toEpochMilli()));
            ResultSet result = read.executeQuery();
            while (result.next()) {
                temperatur = result.getDouble(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return temperatur;
    }
}
