package src;

import ithakimodem.*;
import java.io.File;
import java.io.FileWriter;

public class ARQ {
  /**
   * Automatic repeat request
   *
   * @param modem
   * @param ackCode
   * @param nackCode
   * @return The number of nack packets
   */
  private static Integer run(Modem modem, String ackCode, String nackCode) {
    System.out.println("\nARQ application...");
    Integer xorResult = 0, fcs = 0;

    String message = Echo.pstop(modem, ackCode);
    System.out.println("ACK:  " + message);
    Integer counter = 0;

    while (true) {
      // parse message to find the encoding sequence
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
    return counter;
  }

  private static Integer xorCharArray(char[] array) {
    int xorResult = (int)array[0];
    for (int i = 1; i < array.length; i++) {
      xorResult = xorResult ^ array[i];
    }

    return xorResult;
  }

  public static void arqRepeat(Modem modem, String ackCode, String nackCode,
                               long timeInterval) {
    float start = System.currentTimeMillis() / 1000f;
    int ackCounter = 1, nackCounter = 0;
    try (FileWriter arq = new FileWriter(new File("logs/arq.txt"))) {
      while ((System.currentTimeMillis() / 1000f - start) < timeInterval) {
        System.out.print("Packet No" + ackCounter + ": ");

        long tic = System.currentTimeMillis();
        nackCounter += ARQ.run(modem, ackCode, nackCode);
        long toc = System.currentTimeMillis();

        System.out.println("Total time: " + (toc - tic) / 1000.0 + " (s)\n");
        ackCounter += 1;

        arq.write((toc - tic) / 1000.0 + "\n");
      }
    } catch (Exception x) {
      System.out.println(x);
    }

    System.out.println("BER:" + berCalculation(ackCounter, nackCounter));
  }

  private static double berCalculation(int ackCounter, int nackCounter) {
    float successProbability = ackCounter / (float)(nackCounter + ackCounter);
    System.out.println("Success prob: " + successProbability * 100 + "%");
    
    final int numberOfEncodedChars = 16, bitsPerByte = 8; 
    int bitsSequence = numberOfEncodedChars * bitsPerByte;
    
    double ber = 1 - Math.pow(successProbability, 1 / (float)bitsSequence);

    return ber;
  }
}
