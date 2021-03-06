package de.swm.wetterstation.server;

import com.tinkerforge.IPConnection;
import de.swm.wetterstation.FxController;

/**
 * Created by luca on 12.07.2017.
 */
public class main {
    private static final String HOST = "192.168.178.96";
    private static final int PORT = 4223;
    private static IPConnection ipcon = null;
    private static WeatherListener weatherListener = null;

    public static void main(String args[]) {
        ipcon = new IPConnection();

        while(true) {
            try {
                ipcon.connect(HOST, PORT);
                break;
            } catch(com.tinkerforge.TinkerforgeException e) {
            }

            try {
                Thread.sleep(1000);
            } catch(InterruptedException ei) {
            }
        }

        weatherListener = new WeatherListener(ipcon);
        ipcon.addEnumerateListener(weatherListener);
        ipcon.addConnectedListener(weatherListener);

        while(true) {
            try {
                ipcon.enumerate();
                break;
            } catch(com.tinkerforge.NotConnectedException e) {
            }

            try {
                Thread.sleep(1000);
            } catch(InterruptedException ei) {
            }
        }

        System.out.println("Wetter vong Heute");


    }
}
