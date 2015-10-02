import org.apache.log4j.Logger;

import java.util.Arrays;

/**
 * Java Main program to act as client using the Crawler application.
 */
public class Main {

    /*
    * Using apache log4j to enable contextual logging through out the application
    * Aids in the diagnostic of any problems that may occur.
    */
    static Logger LOG = Logger.getLogger(Main.class);

    public static void main(String[] args) {
        LOG.info(Arrays.toString(args));
        LOG.info("Beginning crawl.");

        Crawler.run(args[0], 1);

        LOG.info("Crawl successfully completed.");
    }

}
