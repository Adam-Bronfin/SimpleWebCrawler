import com.ibatis.common.jdbc.ScriptRunner;
import java.io.*;
import java.sql.*;
import java.util.Map;
import java.util.Properties;

/**
 * FrequencyDistributionDAO is a data access object that allows us to persist a new frequency distribution.
 * save() allows us to persist a new freq distribution.
 * refreshDB() allows us to clear out our database before a new crawl begins.
 */
public class FrequencyDistributionDAO {

    /**
     * FrequencyDistributionDAO save method will take an object that represents a FrequencyDistribution and persist it to the database.
     * If the source associated with the freq distribution already exists in the DB, it will cancel the operation.
     * @param frequencyDistribution FrequencyDistribution object to be written to the database.
     */
    public static void save(FrequencyDistribution frequencyDistribution) {
        try {
            Connection connection = getConnection();

            Integer source_id = null;

            PreparedStatement selectSourceExists = connection.prepareStatement(getSourceSelect());
            selectSourceExists.setString(1, frequencyDistribution.source);
            ResultSet resultSet = selectSourceExists.executeQuery();

            /* Sanity Check
             * If the source we are attempting to save is already present, then cancel the save() operation.
             */
            if (resultSet.next()) {
                return;
            } else {
                //Insert a new record into sources for this frequency distributions source
                PreparedStatement insertSource = connection.prepareStatement(getSourceInsert());
                insertSource.setString(1, frequencyDistribution.source);
                insertSource.execute();

                //Select the newly created source ID to use in the insert statements of our word counts
                PreparedStatement selectSourceID = connection.prepareStatement(getSourceSelect());
                selectSourceID.setString(1, frequencyDistribution.source);
                ResultSet rs = selectSourceID.executeQuery();

                //Null check to make rs is not an empty set.
                if (rs.next()) {
                    source_id = rs.getInt("id");
                }
            }

            /*
            * For each entry in the frequency dist, insert the word and count into frequency_distribution table
            * If the word already exists for the source, update it's count as count = (count + new_count)
            */
            for (Map.Entry<String, Integer> entry : frequencyDistribution.freqDist.entrySet()) {
                try {
                    String insert = getFreqDistInsert();
                    PreparedStatement insertWordCount = connection.prepareStatement(insert);
                    insertWordCount.setString(1, entry.getKey());
                    insertWordCount.setInt(2, entry.getValue());
                    insertWordCount.setInt(3, source_id);
                    insertWordCount.execute();
                } catch (Exception e) {
                    Main.LOG.warn("Error executing word insert statement for " + entry.getKey());
                    e.printStackTrace();
                }
            }
        } catch (SQLException e) {
            Main.LOG.fatal("Error executing sources INSERT statement.");
            e.printStackTrace();
        }
    }

    /**
     * Method recreates the Crawler DB from CreateWebCrawler.sql file.
     * This method can be used so that the database will reflect the frequency distribution of the last run.
     */
    public static void refreshDB() {
        String createDBScript = "CreateWebCrawlerDB.sql";

        Connection connection = getConnection();
        try {
            // Initialize object for ScripRunner
            ScriptRunner sr = new ScriptRunner(connection, false, false);

            // Give the input file to Reader
            Reader reader = new BufferedReader(
                    new FileReader(createDBScript));

            // Execute script
            sr.runScript(reader);

        } catch (Exception e) {
            Main.LOG.fatal("Failed to execute: " + createDBScript);
        }
    }

    /*
    * getConnection() opens and returns a connection to our local DB WebCrawler with the user credentials
    * specified in mysql.properties
    */
    private static Connection getConnection() {
        Connection connection = null;
        try {
            /*Forces MySQL driver to load and initialize itself*/
            Class.forName("com.mysql.jdbc.Driver");
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Properties properties = new Properties();
            InputStream input = null;
            String username = null;
            String password = null;
            try {
                input = new FileInputStream("mysql.properties");
                properties.load(input);
                username = properties.getProperty("user");
                password = properties.getProperty("password");
            } catch (FileNotFoundException e) {
                Main.LOG.fatal("Error finding MySQL credentials file.");
                e.printStackTrace();
            } catch (IOException e) {
                Main.LOG.fatal("Error connecting to MySQL credentials file.");
                e.printStackTrace();
            }
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/WebCrawler", username, password);

        } catch (Exception e) {
            Main.LOG.fatal("Error opening connection to MySQL DB.");
            e.printStackTrace();
        }
        return connection;
    }

    /* Returns a parametrized SQL insert statement into the frequency_distribution table. */
    private static String getFreqDistInsert() {
        StringBuilder insertBuilder = new StringBuilder();
        insertBuilder.append("INSERT INTO frequency_distribution ");
        insertBuilder.append("(word, count, source_id) ");
        insertBuilder.append("VALUES ");
        insertBuilder.append("(?, ?, ?) ");
        insertBuilder.append("on duplicate key update count = count + values(count);");
        return insertBuilder.toString();
    }

    /* Returns a parametrized SQL REPLACE statement into the frequency_distribution table. */
    private static String getSourceInsert() {
        StringBuilder insertBuilder = new StringBuilder();
        insertBuilder.append("REPLACE INTO SOURCES ");
        insertBuilder.append("(url) ");
        insertBuilder.append("VALUES ");
        insertBuilder.append("(?);");
        return insertBuilder.toString();
    }

    /* Returns a parametrized SQL Select statement to return the source_id of the URL specified, if it's present in the table. */
    private static String getSourceSelect() {
        StringBuilder selectBuilder = new StringBuilder();
        selectBuilder.append("SELECT id from sources ");
        selectBuilder.append("where ");
        selectBuilder.append("url=?;");
        return selectBuilder.toString();
    }
}
