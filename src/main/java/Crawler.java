import org.apache.commons.validator.routines.UrlValidator;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.*;

/**
 * Crawler is the entry point into our crawling application.
 * Crawler.run() allows us to pass along an initial URL, and a parameter "totalURLsToFollow"
 * which determines how many URLs in total that are scraped.
 */
public class Crawler {

    /**
     * Queue to represent my frontier of URLs to visit. Items are added to the Queue as they're seen.
     */
    private static Queue<String> URL_FRONTIER = new LinkedList<String>();

    /**
     * HashSet to store all URLs we've visited before so we don't visit them again.
     */
     private static Set<String> VISITED = new HashSet<String>();

    /**
     * Run method of Crawler used to initiate crawl.
     * Accepts a valid URL prefixed by http or https and builds a frequency distribution from all content words discovered.
     * which is then persisted to a local database named WebCrawler.
     * @param url URL to begin crawling from. URLs found on this page will be added to the URL frontier.
     * @param totalURLsToFollow Determines how many more pages to crawl from links found on initial URL.
     */
    public static void run(String url, Integer totalURLsToFollow) {
        /* Clears the DB so that the database always represents the persisted frequency distribution(s) of the last crawl. */
        FrequencyDistributionDAO.refreshDB();
        Document doc = null;
        URL_FRONTIER.add(url);
        int count = 0;
        while (count < totalURLsToFollow && !URL_FRONTIER.isEmpty()) {
            String URL = URL_FRONTIER.poll();
            if (isValidUrl(URL) && !VISITED.contains(URL)) {
                VISITED.add(URL);
                Connection connection = Jsoup.connect(URL).timeout(5000);
                try {
                    doc = connection.get();
                    addToURLFrontier(doc);
                    FrequencyDistribution frequencyDistribution = DOMParser.buildFreqDist(doc);
                    FrequencyDistributionDAO.save(frequencyDistribution);
                    count++;
                } catch (Exception e) {
                    Main.LOG.warn("Error to connecting to URL: " );
                }
            } else {
                Main.LOG.warn("Skipping crawl of invalid URL: " + URL_FRONTIER.poll());
            }
        }
    }

    /*Uses apache commons UrlValidator to assess whether a given URL is valid and able to be crawled. */
    private static boolean isValidUrl(String url) {
        String[] schemes = {"http", "https"};
        UrlValidator validator = new UrlValidator(schemes);
        return validator.isValid(url);
    }

    /*This method adds to URL_FRONTIER all URLs seen on a given DOM represented by doc. */
    private static void addToURLFrontier(Document doc) {
        List<String> urls = DOMParser.getScrapedURLs(doc);
        for (String url : urls) {
            URL_FRONTIER.add(url);
        }
    }
}
