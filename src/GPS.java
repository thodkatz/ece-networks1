package src;

import ithakimodem.*;
import java.util.ArrayList;

public class GPS {
  private static ArrayList<String> parser(Modem modem, String code) {
    ArrayList<String> coordinates = new ArrayList<String>();

    modem.write(code.getBytes());
    while (true) {
      try {
        String line = returnLine(modem);
        System.out.println(line);

        // check for breaking flag
        if (line.equals("STOP ITHAKI GPS TRACKING\r"))
          break;

        // skip first message and track longitude and latitude
        if (!line.equals("START ITHAKI GPS TRACKING\r")) {
          String[] nmea_split = line.split(",");
          coordinates.add(nmea_split[1]); // time
          coordinates.add(nmea_split[2]); // latitude
          coordinates.add(nmea_split[4]); // longitude
        }

      } catch (Exception x) {
        System.out.println(x);
      }
    }
    System.out.println();

    return coordinates;
  }

  /**
   * Create a formated string merging gps data points
   */
  public static String stringDataPoints(Modem modem, String code,
                                        int numPoints) {
    String mergeDataPoints = "";
    String[] adjust_coords = {"", ""};

    // coordinates: time, latitude, longitude
    ArrayList<String> coordinates = new ArrayList<String>();
    coordinates = parser(modem, code);
    System.out.println(coordinates);

    // adjust to minutes
    for (int i = 0; i < coordinates.size(); i++) {
      if (i % 3 == 0) {
        mergeDataPoints += "T";
      }
      if (!(i % 3 == 0)) {
        String lat_long = coordinates.get(i);
        String[] intAndDecimal = lat_long.split("\\.");
        
        int intPart = Integer.parseInt(i%2==1 ? intAndDecimal[0].substring(0,2) : intAndDecimal[0].substring(0,3));
        Float floatPart = Float.parseFloat(intAndDecimal[1]);

        String hour = String.valueOf(intPart);
        String minute = String.valueOf(floatPart * 0.6f);
        minute = minute.substring(0, 2);

        adjust_coords[i % 3 - 1] = hour + minute;
      }
      if (i % 3 == 0) {
        // first store the longitude and after the latitude
        mergeDataPoints += adjust_coords[1];
        mergeDataPoints += adjust_coords[0];
      }
    }

    System.out.println(mergeDataPoints);
    return mergeDataPoints;
  }

  private static String returnLine(Modem modem) {
    String line = "";
    char returnCharModem = ' ';

    while (true) {
      try {
        returnCharModem = (char)modem.read();
        if (returnCharModem != '\n')
          line += returnCharModem;
        else
          break;
      } catch (Exception x) {
        System.out.println(x);
      }
    }

    return line;
  }
}
