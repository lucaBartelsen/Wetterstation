package de.swm.wetterstation.neu;

import com.tinkerforge.AlreadyConnectedException;
import com.tinkerforge.IPConnection;
import com.tinkerforge.NetworkException;

/**
 * Created by luca on 05.07.2017.
 */
public class main {
    private static final String HOST = "192.168.178.96";
    private static final int PORT = 4223;
    private static IPConnection ipcon = null;

    public static void main(String[] args) {
        ipcon = new IPConnection();
        try {
            ipcon.connect(HOST, PORT);
        } catch (NetworkException e) {
            e.printStackTrace();
        } catch (AlreadyConnectedException e) {
            e.printStackTrace();
        }
    }
}
