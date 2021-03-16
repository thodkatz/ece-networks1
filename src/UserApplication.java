package src;

import ithakimodem.*;
import java.io.File;
import java.util.Scanner;

class UserApplication {
  public static void main(String[] args) {
    printWelcome();

    Modem modem = new Modem();
    int speed = 80_000, timeout = 2_000;
    setupModem(modem, speed, timeout);
    startModem(modem, false);

    // Request codes

    String echoCode = "E4093\r";
    String imageNoErrorCode = "M9420\r";
    String imageWithErrorCode = "G8706\r";
    String gpsCode = "P8315\r";
    String ackCode = "Q7620\r";
    String nackCode = "R1577\r";

    // applications

    int numPackets = 5;
    for (int i = 0; i < numPackets; i++) {
      System.out.print("Packet No" + i + ": ");

      long tic = System.currentTimeMillis();
      Echo.pstop(modem, echoCode);
      long toc = System.currentTimeMillis();

      System.out.println("Total time: " + (toc - tic) / 1000.0 + " (s)\n");
    }

    // Echo.generic(modem, echoCode);
    // Echo.generic(modem, gpsCode);
    // Echo.generic(modem, ackCode);
    // Echo.generic(modem, nackCode);
    // Image.get(modem, imageNoErrorCode);
    Image.get(modem, imageWithErrorCode);

    // testModem(modem);

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