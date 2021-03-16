package src;
import ithakimodem.*;

public class Echo {

  /**
   * Echo packet contain time info
   *
   * @param modem The virtual opened modem
   * @param numPackets The requested number of echo time packets
   */
  public static void time(Modem modem, String code, int numPackets) {
    System.out.print("Echo application");

    final int finishReadingFlag = -1;

    int returnValueModem = 0;
    char returnCharModem = ' ';

    for (int i = 0; i < numPackets; i++) {
      modem.write(code.getBytes());
      while (true) {
        try {
          returnValueModem = modem.read();
          returnCharModem = (char)returnValueModem;

          System.out.print(returnCharModem);
          Thread.sleep(10);

          // check for breaking flag
          if (returnValueModem == finishReadingFlag)
            break;
        } catch (Exception x) {
          System.out.println(x);
        }
      }
      System.out.println();
    }
  }
}
