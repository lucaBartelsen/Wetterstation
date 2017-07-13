package de.swm.wetterstation.server;

import org.postgresql.ds.PGSimpleDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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
}
