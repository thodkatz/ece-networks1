package src;
import ithakimodem.*;
import java.io.File;
import java.io.FileWriter;
import java.util.LinkedList;
import java.util.Queue;

public class Echo {

  /**
   * Echo packet contain info dependent of the request code.
   *
   * Stop communication when detect "PSTOP"
   *
   * @param modem The virtual opened modem
   * @param code Echo request code
   */
  public static String pstop(Modem modem, String code) {
    // System.out.println("Echo application");

    char returnModem = ' ';
    String message = "";
    Queue<Character> breakingChars = new LinkedList<>();

    modem.write(code.getBytes());

    while (true) {
      try {

        // all the echo packets ends to "PSTOP"
        if (breakingChars.size() != 5) {
          returnModem = (char)modem.read();
          breakingChars.add(returnModem);
        } else {
          String breakingString = queueChar2String(breakingChars);
          if (breakingString.equals("PSTOP"))
            break;
          else {
            breakingChars.remove();
            returnModem = (char)modem.read();
            breakingChars.add(returnModem);
          }
        }
      } catch (Exception x) {
        System.out.println(x);
      }

      message += returnModem;
    }

    return message;
  }

  /**
   * Stop communication when -1 returns
   *
   * @param modem
   * @param code
   */
  public static void generic(Modem modem, String code) {
    final int finishReadingFlag = -1;

    int returnValueModem = 0;
    char returnCharModem = ' ';

    modem.write(code.getBytes());
    while (true) {
      try {
        returnValueModem = modem.read();
        returnCharModem = (char)returnValueModem;

        System.out.print(returnCharModem);

        // check for breaking flag
        if (returnValueModem == finishReadingFlag)
          break;
      } catch (Exception x) {
        System.out.println(x);
      }
    }
    System.out.println();
  }

  public static void pstopRepeat(Modem modem, String code, long timeInterval) {
    float start = System.currentTimeMillis() / 1000f;
    int counter = 0;

    try (FileWriter echo = new FileWriter(new File("logs/echo.txt"))) {
      while ((System.currentTimeMillis() / 1000f - start) < timeInterval) {
        System.out.print("Packet No" + counter + ": ");

        long tic = System.currentTimeMillis();
        String message = Echo.pstop(modem, code);
        System.out.println(message);
        long toc = System.currentTimeMillis();

        System.out.println("Total time: " + (toc - tic) + " (ms)\n");
        counter += 1;

        echo.write((toc - tic) + "\n");
      }
    } catch (Exception x) {
      System.out.println(x);
    }
  }

  private static String queueChar2String(Queue<Character> queue) {
    String message = "";
    for (Character character : queue) {
      message += character;
    }
    return message;
  }
}