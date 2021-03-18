package src;

import ithakimodem.*;

public class ARQ {
  private static void run(Modem modem, String ackCode, String nackCode) {
    System.out.println("\nARQ application...");
    Integer xorResult = 0, fcs = 0;

    String message = Echo.pstop(modem, ackCode);
    System.out.println("ACK:  " + message);
    Integer counter = 0;

    while (true) {
      String[] parsedAck = message.split("\\s+");
      String encodedSequence =
          parsedAck[4].substring(1, parsedAck[4].length() - 1);
      char[] encodedChar = encodedSequence.toCharArray();

      xorResult = xorCharArray(encodedChar);
      fcs = Integer.parseInt(parsedAck[5]);
      System.out.println("Xor: " + xorResult + " FCS: " + fcs);

      if (xorResult != fcs) {
        message = Echo.pstop(modem, nackCode);
        System.out.println("NACK: " + message);
        counter += 1;
      } else {
        break;
      }
    }
    System.out.println("Number of nack " + counter);
  }

  private static Integer xorCharArray(char[] array) {
    int xorResult = (int)array[0];
    for (int i = 1; i < array.length; i++) {
      xorResult = xorResult ^ array[i];
    }

    return xorResult;
  }

  public static void arqRepeat(Modem modem, String ackCode, String nackCode, Integer numPackets) {
    for (int i = 0; i < numPackets; i++) {
      ARQ.run(modem, ackCode, nackCode);
    }
  }
}
