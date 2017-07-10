package de.swm.wetterstation.test;

import java.sql.SQLException;

/**
 * Created by luca on 07.07.2017.
 */
public class dbLogic {
    JDBC jdbc = null;

    public dbLogic() {
        try {
            jdbc = new JDBC();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void lowestTemperatur24(){
        double temperatur = jdbc.getTemperatur();
    }
}
