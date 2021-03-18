package src;

import ithakimodem.*;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

public class GPS {
  private static ArrayList<String> parser(Modem modem, String code) {
    ArrayList<String> coordinates = new ArrayList<String>();

    modem.write(code.getBytes());
    while (true) {
      try {
        String line = returnLine(modem);
        // System.out.println(line);

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

    return coordinates;
  }

  /**
   * Create a formated string merging gps data points
   */
  public static String mergeDataPoints(Modem modem, String code,
                                       int numPoints) {
    System.out.println("Creating GPS parameter special format...");
    String finalPoints = "";
    String[] adjust_coords = {"", ""};

    // coordinates: time, latitude, longitude
    ArrayList<String> coordinates = new ArrayList<String>();
    // filtered: merge latitude and longitude
    ArrayList<String> filteredCoords = new ArrayList<String>();

    coordinates = parser(modem, code);

    // adjust to degrees, minutes, seconds
    for (int i = 0; i < coordinates.size(); i++) {
      if (!(i % 3 == 0)) {
        String lat_long = coordinates.get(i);

        String hour =
            i % 3 == 1 ? lat_long.substring(0, 2) : lat_long.substring(1, 3);

        String minutes = i % 3 == 1 ? lat_long.substring(2, lat_long.length())
                                    : lat_long.substring(3, lat_long.length());
        minutes = String.valueOf(Float.parseFloat(minutes));
        int intPart = Integer.parseInt(minutes.split("\\.")[0]);
        float decimalPart = Float.parseFloat("0." + minutes.split("\\.")[1]);
        minutes = String.valueOf(intPart);

        String seconds = String.valueOf(decimalPart * 60f);
        seconds = seconds.substring(0, 2);

        adjust_coords[i % 3 - 1] = hour + minutes + seconds;
      }
      if (i % 3 == 2) {
        // first store the longitude and after the latitude
        filteredCoords.add("T=" + adjust_coords[1] + adjust_coords[0]);
      }
    }

    // keep track of the indices to find the timestamps of the unique data
    // points
    ArrayList<Integer> indices = new ArrayList<Integer>();
    finalPoints = findUniqueDataPoints(filteredCoords, indices);

    ArrayList<String> timestamps = new ArrayList<String>();
    for (Integer i : indices) {
      timestamps.add(coordinates.get(i * 3));
    }

    try (FileWriter time = new FileWriter(new File("logs/gps_timstamps"))) {
      for (String string : timestamps) {
        time.write(string + "\n");
      }
    } catch (Exception x) {
      System.out.println(x);
    }

    return finalPoints;
  }

  /**
   * Find different GPS points based on latitude and longitude
   *
   * @param coordinates Format T=AABBCCDDEEFF longitude and latitude in hourse,
   *     minutes, seconds
   * @return
   */
  private static String findUniqueDataPoints(ArrayList<String> coordinates,
                                             ArrayList<Integer> indices) {
    ArrayList<String> filtered = new ArrayList<String>();
    int lengthSamples = Math.min(4, coordinates.size());
    String parsedString = "";

    // exclude "T=" and find unique data points
    for (int i = 0; i < coordinates.size(); i++) {
      if (!filtered.contains(coordinates.get(i))) {
        filtered.add(coordinates.get(i));
        indices.add(i);
      }
    }

    for (int i = 0; i < filtered.size(); i++) {
      parsedString += filtered.get(i);
    }

    return parsedString;
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
