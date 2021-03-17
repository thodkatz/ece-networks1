package src;
import ithakimodem.*;

public class Echo {

  /**
   * Echo packet contain info dependent of the request code.
   *
   * Stop communication when detect "PSTOP"
   *
   * @param modem The virtual opened modem
   * @param code Echo request code
   */
  public static void pstop(Modem modem, String code) {
    // System.out.println("Echo application");

    char first, second = ' ';

    modem.write(code.getBytes());
    first = (char)modem.read();
    System.out.print(first);

    while (true) {
      try {
        second = (char)modem.read();
        System.out.print(second);

        // all the echo packets ends to "PSTOP"
        // check if message ending "OP" (cheat yep) to avoid waiting for
        // breaking -1 that lags the communication
        if (first == 'O' && second == 'P')
          break;

        first = second;

      } catch (Exception x) {
        System.out.println(x);
      }
    }
    System.out.println();
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

  public static void pstopRepeat(Modem modem, String code, int numPackets) {
    for (int i = 0; i < numPackets; i++) {
      System.out.print("Packet No" + i + ": ");

      long tic = System.currentTimeMillis();
      Echo.pstop(modem, code);
      long toc = System.currentTimeMillis();

      System.out.println("Total time: " + (toc - tic) / 1000.0 + " (s)\n");
    }
  }
}
