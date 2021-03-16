package src;
import ithakimodem.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

public class Image {
  public static void get(Modem modem, String code) {
    System.out.println("Image application");

    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    int returnValueModem = 0;
    byte first, second;

    long tic = System.currentTimeMillis();

    modem.write(code.getBytes());
    while (true) {
      try {
        returnValueModem = modem.read();
        first = int0(returnValueModem);
        buffer.write(first);

        returnValueModem = modem.read();
        second = int0(returnValueModem);
        buffer.write(second);

        // System.out.print(String.format("%02X", first));
        // System.out.print(String.format("%02X", second));

        if ((String.format("%02X", first).equals("FF")) &&
            (String.format("%02X", second)).equals("D9"))
          break;
      } catch (Exception x) {
        System.out.println(x);
      }
    }
    byte[] dataImage = buffer.toByteArray();

    String path = "image.jpg";
    File image = new File(path);
    try (FileOutputStream fos = new FileOutputStream(image)) {
      fos.write(dataImage);
      System.out.println("File " + path + " has been created successfully");
    } catch (Exception x) {
      System.out.println(x);
    }

    long toc = System.currentTimeMillis();
    System.out.print("Total time creating image: " + (toc - tic) / 1000.0 +
                     " (s)");
  }

  /**
   * source:
   * https://stackoverflow.com/questions/1936857/convert-integer-into-byte-array-java
   */
  private static byte int3(int x) { return (byte)(x >> 24); }
  private static byte int2(int x) { return (byte)(x >> 16); }
  private static byte int1(int x) { return (byte)(x >> 8); }
  private static byte int0(int x) { return (byte)(x >> 0); }
}
