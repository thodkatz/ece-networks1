package src;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import java.util.logging.*;
import java.util.List;

public class WebScraping {
  public static String[] getCodes() {
    String[] cleanCodes = new String[6];
    try (final WebClient webClient = new WebClient()) {
      System.out.println("...begin scraping request codes...");
      // disable warnings
      java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF); 
      
      // Get the first page
      final HtmlPage page1 =
          webClient.getPage("http://ithaki.eng.auth.gr/netlab/");

      final List<HtmlForm> forms = page1.getForms();
      final HtmlTextInput firstName = forms.get(0).getInputByName("fi");
      final HtmlTextInput lastName = forms.get(0).getInputByName("fa");
      final HtmlTextInput aem = forms.get(0).getInputByName("am");

      // login
      firstName.type("theod");
      lastName.type("katz");
      aem.type("9282");

      // Get the second page
      final HtmlPage page2 =
          (page1.getElementsByTagName("button")).get(0).click();

      List<HtmlDivision> getDivLink = page2.getByXPath("//div[@class='start']");
      List<HtmlElement> showCodes = getDivLink.get(0).getElementsByAttribute(
          "span", "onclick", "action('1')");

      // Get the third page
      final HtmlPage page3 = showCodes.get(0).click();
      List<HtmlDivision> getDivCodes = page3.getByXPath("//div[@class='main']");
      List<HtmlElement> codes =
          getDivCodes.get(0).getElementsByAttribute("font", "color", "red");

      cleanCodes[0] = codes.get(0).getTextContent().substring(1, 6);
      cleanCodes[1] = codes.get(1).getTextContent().substring(1, 6);
      cleanCodes[2] = codes.get(2).getTextContent().substring(1, 6);
      cleanCodes[3] = codes.get(3).getTextContent().substring(1, 6);
      cleanCodes[4] = codes.get(4).getTextContent().substring(1, 6);
      cleanCodes[5] = codes.get(5).getTextContent().substring(1, 6);
    } catch (Exception x) {
      System.out.println(x + "\n"
                         + "Failed scrape request codes. Aborting...");
      System.exit(1);
    }
    for(String i : cleanCodes) System.out.println(i);
    System.out.println("...end scraping request codes...");
    return cleanCodes;
  }
}