import org.jsoup.select.Elements;
import org.jsoup.nodes.Document;
import java.util.*;

/**
 * DOMParser allows us to take an Object representation of an HTML document and extract a frequency distribution.
 * buildFreqDist() allows us to obtain a frequency distribution.
 * getScrapedURLs() allows us to extract out all newly seen URLs we may want to visit later.
 */
public class DOMParser {

    /*Map to contain keys representing unique words mapped to their frequency count*/
    private static Map<String, Integer> map = new HashMap<String, Integer>();

    /*Filter to cleanse text of everything but upper or lowercase letters*/
    private static String SPECIAL_CHARACTER_FILTER = "[^A-Za-z]";

    /**
     * Builds a frequency distribution of content words from an object representation of an HTML document.
     * @param doc Document object that is used to construct a frequency distribution.
     * @return FrequencyDistribution
     */
    public static FrequencyDistribution buildFreqDist(Document doc) {
        String source = doc.baseUri();

        extractContent(doc);

        return new FrequencyDistribution(source, map);
    }

    /*
    * Enumerates over all HTML elements found within the DOM's body, and extracts out all words from the text in each tag.
    * Special char filter is applied to remove unwanted text.
    */
    private static void extractContent(Document doc) {
        Elements elements = doc.body().getAllElements();
        for (int i = 0; i < elements.size(); i++) {
            insertIntoMap(
                    elements.get(i)
                            .text()
                            .replaceAll(SPECIAL_CHARACTER_FILTER, " ")
                            .split("\\s+")
            );
        }
        Main.LOG.info("Finished extracting content words from: " + doc.baseUri());
    }

    /**
     * Get scraped URL returns all URL's that can be added to our URL frontier.
     * @param doc Document object that is used to construct a frequency distribution.
     * @return Returns a list of URLs found on the provided Document.
     */
    public static List<String> getScrapedURLs(Document doc) {
        List<String> list = new ArrayList<String>();

        Elements urls = doc.select("a");
        for (int i = 0; i < urls.size(); i++) {
            list.add(urls.get(i).absUrl("href"));
        }
        return list;
    }

    /* Method to insert words into our frequency distribution as they're discovered. */
    private static void insertIntoMap(String[] words) {
        for (String word : words) {
            if (word.length() > 0 && word.length() < 25) {
                if (map.containsKey(word.toLowerCase())) {
                    map.put(word.toLowerCase(), map.get(word.toLowerCase()) + 1);
                } else {
                    map.put(word.toLowerCase(), 1);
                }
            }
        }
    }
}
