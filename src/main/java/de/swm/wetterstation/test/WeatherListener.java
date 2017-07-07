package de.swm.wetterstation.test;

import com.tinkerforge.*;

import java.sql.SQLException;

/**
 * Created by luca on 05.07.2017.
 */
class WeatherListener implements IPConnection.EnumerateListener,
        IPConnection.ConnectedListener,
        BrickletAmbientLight.IlluminanceListener,
        BrickletAmbientLightV2.IlluminanceListener,
        BrickletHumidity.HumidityListener,
        BrickletBarometer.AirPressureListener,
        BrickletLCD20x4.ButtonPressedListener {
    private IPConnection ipcon = null;
    private BrickletLCD20x4 brickletLCD = null;
    private BrickletAmbientLight brickletAmbientLight = null;
    private BrickletAmbientLightV2 brickletAmbientLightV2 = null;
    private BrickletHumidity brickletHumidity = null;
    private BrickletBarometer brickletBarometer = null;
    private JDBC jdbc = null;

    public WeatherListener(IPConnection ipcon) {
        this.ipcon = ipcon;
        try {
            jdbc = new JDBC(this);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void illuminance(int illuminance) {
        if(brickletLCD != null) {
            String text = String.format("Illuminanc %6.2f lx", illuminance/10.0);
            try {
                brickletLCD.writeLine((short)0, (short)0, text);
            } catch(com.tinkerforge.TinkerforgeException e) {
            }

        }
    }

    public void illuminance(long illuminance) {
        if(brickletLCD != null) {
            try {
            jdbc.updateQuery(illuminance/100.0, "helligkeit");
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
    }

    public void humidity(int humidity) {
        if(brickletLCD != null) {
            try {
            jdbc.updateQuery(humidity/10.0,"luftfeuchtigkeit");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void airPressure(int airPressure) {
        if(brickletLCD != null) {
            try {
            jdbc.updateQuery(airPressure/1000.0, "luftdruck");
            } catch (SQLException e) {
                e.printStackTrace();
            }

            int temperature;
            try {
                temperature = brickletBarometer.getChipTemperature();
            } catch(com.tinkerforge.TinkerforgeException e) {
                System.out.println("Could not get temperature: " + e);
                return;
            }

            // 0xDF == ° on LCD 20x4 charset
            try {
            jdbc.updateQuery(temperature/100.0,"temperatur");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void enumerate(String uid, String connectedUid, char position,
                          short[] hardwareVersion, short[] firmwareVersion,
                          int deviceIdentifier, short enumerationType) {
        if(enumerationType == IPConnection.ENUMERATION_TYPE_CONNECTED ||
                enumerationType == IPConnection.ENUMERATION_TYPE_AVAILABLE) {
            if(deviceIdentifier == BrickletLCD20x4.DEVICE_IDENTIFIER) {
                try {
                    brickletLCD = new BrickletLCD20x4(uid, ipcon);
                    brickletLCD.clearDisplay();
                    brickletLCD.backlightOn();
                    brickletLCD.addButtonPressedListener(this);
                    System.out.println("LCD 20x4 initialized");
                } catch(com.tinkerforge.TinkerforgeException e) {
                    brickletLCD = null;
                    System.out.println("LCD 20x4 init failed: " + e);
                }
            } else if(deviceIdentifier == BrickletAmbientLight.DEVICE_IDENTIFIER) {
                try {
                    brickletAmbientLight = new BrickletAmbientLight(uid, ipcon);
                    brickletAmbientLight.setIlluminanceCallbackPeriod(30000);
                    brickletAmbientLight.addIlluminanceListener(this);
                    System.out.println("Ambient Light initialized");
                } catch(com.tinkerforge.TinkerforgeException e) {
                    brickletAmbientLight = null;
                    System.out.println("Ambient Light init failed: " + e);
                }
            } else if(deviceIdentifier == BrickletAmbientLightV2.DEVICE_IDENTIFIER) {
                try {
                    brickletAmbientLightV2 = new BrickletAmbientLightV2(uid, ipcon);
                    brickletAmbientLightV2.setConfiguration(BrickletAmbientLightV2.ILLUMINANCE_RANGE_64000LUX,
                            BrickletAmbientLightV2.INTEGRATION_TIME_200MS);
                    brickletAmbientLightV2.setIlluminanceCallbackPeriod(30000);
                    brickletAmbientLightV2.addIlluminanceListener(this);
                    System.out.println("Ambient Light 2.0 initialized");
                } catch(com.tinkerforge.TinkerforgeException e) {
                    brickletAmbientLightV2 = null;
                    System.out.println("Ambient Light 2.0 init failed: " + e);
                }
            } else if(deviceIdentifier == BrickletHumidity.DEVICE_IDENTIFIER) {
                try {
                    brickletHumidity = new BrickletHumidity(uid, ipcon);
                    brickletHumidity.setHumidityCallbackPeriod(30000);
                    brickletHumidity.addHumidityListener(this);
                    System.out.println("Humidity initialized");
                } catch(com.tinkerforge.TinkerforgeException e) {
                    brickletHumidity = null;
                    System.out.println("Humidity init failed: " + e);
                }
            } else if(deviceIdentifier == BrickletBarometer.DEVICE_IDENTIFIER) {
                try {
                    brickletBarometer = new BrickletBarometer(uid, ipcon);
                    brickletBarometer.setAirPressureCallbackPeriod(30000);
                    brickletBarometer.addAirPressureListener(this);
                    System.out.println("Barometer initialized");
                } catch(com.tinkerforge.TinkerforgeException e) {
                    brickletBarometer = null;
                    System.out.println("Barometer init failed: " + e);
                }
            }
        }
    }

    public void connected(short connectedReason) {
        if(connectedReason == IPConnection.CONNECT_REASON_AUTO_RECONNECT) {
            System.out.println("Auto Reconnect");

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
        }
    }

    @Override
    public void buttonPressed(short i) {
        System.out.println("Button" + i);
        switch (i){
            case 0: break;
            case 1: break;
            case 2: break;
            case 3: break;
            default:
        }

    }

    public void display(String typ, double wert){
        switch (typ){
            case "temperatur": {
                String text = String.format("Temperatur %6.2f %cC", wert, 0xDF);
                System.out.println(text);
                try {
                    brickletLCD.writeLine((short)1, (short)0, text);
                } catch(com.tinkerforge.TinkerforgeException e) {
                }
                break;
            }
            case "helligkeit": {
                String text = String.format("Helligkeit %6.2f Lx", wert);
                System.out.println(text);
                try {
                    brickletLCD.writeLine((short)0, (short)0, text);
                } catch(com.tinkerforge.TinkerforgeException e) {
                }
                break;
            }
            case "luftdruck": {
                String text = String.format("Luftdruck %7.2f mb", wert);
                System.out.println(text);
                try {
                    brickletLCD.writeLine((short)2, (short)0, text);
                } catch(com.tinkerforge.TinkerforgeException e) {
                }
                break;
            }
            case "luftfeuchtigkeit": {
                String text = String.format("Feuchtigkeit %5.2f %%", wert);
                System.out.println(text);
                try {
                    brickletLCD.writeLine((short)3, (short)0, text);
                } catch(com.tinkerforge.TinkerforgeException e) {
                }

                break;
            }
            default:
        }
    }
}