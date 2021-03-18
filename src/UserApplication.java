package src;

import ithakimodem.*;
import java.io.File;
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

    String echoCode = "E9434\r";
    String imageNoErrorCode = "M0549\r";
    String imageWithErrorCode = "G2532=PTZ\r";
    String gpsCode = "P8521";
    String gpsCodeComplete = gpsCode + "R=1000199\r";
    String ackCode = "Q9743\r";
    String nackCode = "R4960\r";

    // applications

    int numPackets = 10;
    // Echo.pstopRepeat(modem, echoCode, numPackets);

    // Echo.generic(modem, echoCode);

    // Echo.generic(modem, gpsCode + "\r");
    // Echo.generic(modem, gpsCode + "R=100011" + "\r");

    // GPS.parser(modem, gpsCode + "\r");
    String maps_query = GPS.mergeDataPoints(modem, gpsCodeComplete, 2);
    System.out.println("The GPS parameter " + maps_query);
    // Image.get(modem, gpsCode + "T=225735403737T=225735403736T=225734403736T=225734403737T=225733403738T=225731403738" + "\r");
    // Image.get(modem, gpsCode + maps_query + "\r");
    
    ARQ.arqRepeat(modem, ackCode, nackCode, numPackets);

    // Image.get(modem, imageNoErrorCode);
    // Image.get(modem, imageWithErrorCode);

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