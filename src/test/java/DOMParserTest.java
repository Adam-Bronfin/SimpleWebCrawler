import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;
import static org.junit.Assert.*;
import org.apache.commons.io.IOUtils;

/**
 * Test to assure that known HTML document fed through DOMParser returns correct frequency distribution
 */
public class DOMParserTest {

   @Test
   /* Tests frequency distribution built on jsoup homepage at http://jsoup.org/ */
   public void testParse() {
       String HTML_DOC = getFile("JSOUP_HOME.html");
       Document doc = Jsoup.parse(HTML_DOC, "http://jsoup.org/");

       FrequencyDistribution frequencyDistribution = DOMParser.buildFreqDist(doc);
       assertTrue(frequencyDistribution.freqDist.containsKey("program"));
       assertEquals(Integer.toUnsignedLong(8), Integer.toUnsignedLong(frequencyDistribution.freqDist.get("program")));

       assertTrue(DOMParser.getScrapedURLs(doc).size() == 43);

   }
    /* Reads a file and returns it as text for mock testing. */
    private String getFile(String fileName){
        String result = "";

        ClassLoader classLoader = getClass().getClassLoader();
        try {
            result = IOUtils.toString(classLoader.getResourceAsStream(fileName));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;

    }
}
