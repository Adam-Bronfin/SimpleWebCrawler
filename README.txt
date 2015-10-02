Included is a web crawler that when provided a URL, and a number of pages to follow, will generate and persist a
frequency distribution of words found for all URLS explored.

If you were to specify "http://yahoo.com" and 10, the web crawler would explore the first URL, as well as the next 10 it
discovered and persist to the DB a frequency distribution of words for each. If you provide the URL and just 1, it will only
build a frequency distribution from that single URL.

For parsing the library Jsoup is used due to it's highly efficient DOM parsing capabilities that allowed me to
focus on the task of visiting web pages and persisting them to the database.

Log4j, an apache commons library, is additionally used through out the application to alert the client about various
states of the crawler, as well as to log any issues that may aid in debugging. This logging library was chosen because
it has very low overhead and offers a lot of context based on the type of log statement being made.

In order to run, you must
A) Have MySQL installed and running locally on your machine.
B) Include your local MySQL username and password in the mysql.properties file so the application can connect to the DB.

Options to run crawler:
1) Load project into an IDE, set up Main class as class for run config.
2) In the terminal, cd into the WebCrawler directory on your machine and run: java -cp out/artifacts/WebCrawler_jar/WebCrawler.jar Main (You may need to change mysql.properties credentials and rebuild jar file.)

The compiled jar file in the /out directory will call Crawler.run("http://yahoo.com/", 1) and extract everything from the
yahoo homepage.