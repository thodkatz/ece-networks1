package src;

import ithakimodem.*;
import java.io.File;
import java.util.Scanner;

import javax.swing.plaf.TreeUI;

class UserApplication {
  public static void main(String[] args) {
    UserApplication.printWelcome();
    UserApplication.helloModem(20_000, 2_000);
  }

  private static void helloModem(int speed, int timeout) {
    Modem modem = new Modem();
    modem.setSpeed(speed);
    modem.setTimeout(timeout);

    modem.open("ithaki");
    //boolean status = modem.write("E8448\n".getBytes());
    //if (status == true) System.out.println("Write buffer unavailable");

    final int finishReadingInt = -1;
    int returnValueModem, finishCounter = 0;
    char[] finishReadingString = {'\r', '\n', '\n', '\n'};

    for (;;) {
      try {
        returnValueModem = modem.read();
        char returnCharModem = (char)returnValueModem;

        System.out.print(returnCharModem);
        Thread.sleep(100);

        // check for breaking return code -1
        // if (returnValueModem == finishReadingInt) break;

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
    modem.close();
  }

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