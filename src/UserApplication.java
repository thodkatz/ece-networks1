package src;

import ithakimodem.*;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Scanner;

class UserApplication {
  public static void main(String[] args) {
    printWelcome();

    Modem modem = new Modem();
    int speed = 80_000, timeout = 2_000;
    setupModem(modem, speed, timeout);
    startModem(modem, false);

    // Request codes

    String echoCode = "E3346";
    String imageNoErrorCode = "M2743";
    String imageWithErrorCode = "G1890";
    String gpsCode = "P4031";
    String gpsCodeComplete = gpsCode + "R=6000199";
    String ackCode = "Q6886";
    String nackCode = "R9981";

    String enter = "\r";

    // write request to file
    try (FileWriter requests = new FileWriter(new File("logs/requests.txt"))) {
      requests.write("Echo: " + echoCode + "\n");
      requests.write("Image No Error: " + imageNoErrorCode + "\n");
      requests.write("Image Yes Error: " + imageWithErrorCode + "\n");
      requests.write("GPS: " + gpsCode + "\n");
      requests.write("GPS Full: " + gpsCodeComplete + "\n");
      requests.write("ACK: " + ackCode + "\n");
      requests.write("NACK: " + nackCode + "\n");
    } catch (Exception x) {
      System.out.println(x);
    }

    // applications

    int minutes = 1;
    final int secondsPerMinute = 60;
    long timeInterval = minutes * secondsPerMinute;
    Echo.pstopRepeat(modem, echoCode + enter, timeInterval);

    // Echo.generic(modem, echoCode);

    // Echo.generic(modem, gpsCode + "\r");
    // Echo.generic(modem, gpsCode + "R=100011" + "\r");

    // String maps_query = GPS.mergeDataPoints(modem, gpsCodeComplete + enter, 2);
    // System.out.println("The GPS parameter " + maps_query);
    // Image.get(modem, gpsCode + maps_query + enter);

    // Image.get(modem, imageNoErrorCode + enter);
    // Image.get(modem, imageWithErrorCode + enter);

    // ARQ.arqRepeat(modem, ackCode + enter, nackCode + enter, numPackets);

    modem.close();
  }

  /**
   * Read the welcome screen when opening the virtual modem
   *
   * @param modem The virtual opened modem
   * @param isPrinted Print the returned message to stdout
   */
  private static void startModem(Modem modem, boolean isPrinted) {
    final int finishReadingFlag = -1;

    int returnValueModem, finishCounter = 0;
    char returnCharModem = ' ';
    char[] finishReadingString = {'\r', '\n', '\n', '\n'};

    while (true) {
      try {
        returnValueModem = modem.read();
        returnCharModem = (char)returnValueModem;

        if (isPrinted) {
          System.out.print(returnCharModem);
          Thread.sleep(10);
        }

        // check for breaking flag
        // if (returnValueModem == finishReadingFlag) break;

        // check for breaking sequence
        if ((returnCharModem == finishReadingString[finishCounter])) {
          finishCounter++;
          if (finishCounter == 4)
            break;
        } else
          finishCounter = 0;

      } catch (Exception x) {
        System.out.println(x);
        break;
      }
    }
  }

  /**
   * Configure modem parameters
   *
   * @param modem The virtual opened modem
   * @param speed The data speed of the communication
   * @param timeout The time interval waiting for message
   */
  private static void setupModem(Modem modem, int speed, int timeout) {
    modem.setSpeed(speed);
    modem.setTimeout(timeout);
    modem.open("ithaki");
  }

  /**
   * Send "TEST" code and print output
   *
   * @param modem The virtual opened modem
   */
  private static void testModem(Modem modem) {
    modem.write("TEST\r".getBytes());
    while (true) {
      int returnMessage = modem.read();
      System.out.print((char)returnMessage);
      if (returnMessage == -1)
        break;
    }
  }

  /**
   * Print welcome ASCII text
   */
  private static void printWelcome() {
    try {
      Scanner welcome = new Scanner(new File("welcome.txt"));
      while (welcome.hasNextLine())
        System.out.println(welcome.nextLine());
    } catch (Exception x) {
      System.out.println(x + "\nWelcome text failed to open.");
    }
  }
}