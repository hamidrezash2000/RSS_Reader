import com.rometools.rome.feed.synd.SyndEntry;
import org.apache.log4j.Logger;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;

public class DB {
    private Sql2o sql2o;
    private static DB ourInstance;
    private static Logger logger = Logger.getLogger(DB.class);

    /**
     * Instance for main program
     * @return Mysql DB
     */
    public static DB getInstance() {
        if (ourInstance == null) {
            Properties properties = getProperty("database.properties");
            ourInstance = new DB(
                    String.format("jdbc:mysql://%s:%s/%s?useUnicode=true&characterEncoding=UTF-8",
                            properties.getProperty("ip"),
                            properties.getProperty("port"),
                            properties.getProperty("database")
                    ),
                    properties.getProperty("username"),
                    properties.getProperty("password")
            );
        }
        return ourInstance;
    }

    /**
     * Instance for tests
     * @return H2 DB
     */
    public static DB getInstanceForTest() {
        if (ourInstance == null) {
            ourInstance = new DB("jdbc:h2:~/rss", null, null);
        }
        return ourInstance;
    }

    /**
     * set sql2o sql connection
     * @param connectionURL
     * @param username
     * @param password
     */
    private DB(String connectionURL, String username, String password) {
        sql2o = new Sql2o(connectionURL, username, password);
    }

    public void insertFeed(Feed feed) {
        try (Connection con = sql2o.open()) {
            con.createQuery(Query.INSERT_FEED)
                    .addParameter("title", feed.getTitle())
                    .addParameter("url", feed.getUrl())
                    .executeUpdate();
        }
    }

    public List<Feed> getAllFeeds() {
        try (Connection con = sql2o.open()) {
            return con.createQuery(Query.GET_ALL_FEEDS)
                    .executeAndFetch(Feed.class);
        }
    }

    public List<Report> getSimilarReports(String title, String link) {
        try (Connection con = sql2o.open()) {
            return con.createQuery(Query.GET_SIMILAR_REPORTS)
                    .addParameter("title", title)
                    .addParameter("link", link)
                    .executeAndFetch(Report.class);
        }
    }

    public void insertReport(Report report) {
        try (Connection con = sql2o.open()) {
            con.createQuery(Query.INSERT_REPORT)
                    .addParameter("title", report.getTitle())
                    .addParameter("link", report.getLink())
                    .addParameter("pubDate", report.getPubDate())
                    .addParameter("description", report.getDescription())
                    .addParameter("feedId", report.getFeedId())
                    .executeUpdate();
        }
    }

    public List<Report> getAllReports() {
        try (Connection con = sql2o.open()) {
            return con.createQuery(Query.GET_ALL_REPORTS)
                    .executeAndFetch(Report.class);
        }
    }

    /**
     * Search in Database With Parameters :
     *
     * @param feedId         : id of feed in database
     * @param toFind         : search text
     * @param lowerBoundDate : lower bound of pubDate
     * @param upperBoundDate : upper bound of pubDate
     * @return List Of Reports
     */
    public List<Report> searchReports(int feedId, String toFind, Date lowerBoundDate, Date upperBoundDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try (Connection con = sql2o.open()) {
            String searchQuery = String.format("SELECT feedId, title, link FROM reports " +
                            "WHERE feedId = %d AND (title LIKE '%%%s%%' OR description LIKE '%%%s%%') AND (pubDate BETWEEN '%s' AND '%s')",
                    feedId, toFind, toFind,
                    dateFormat.format(lowerBoundDate),
                    dateFormat.format(upperBoundDate));
            return con.createQuery(searchQuery)
                    .executeAndFetch(Report.class);
        }

    }

    public boolean reportExists(SyndEntry report) {
        return DB.getInstance().getSimilarReports(report.getTitle(), report.getLink()).size() == 0;
    }

    /**
     * this method returns Properties of given source
     *
     * @param src
     * @return Properties
     */
    private static Properties getProperty(String src) {
        String propertiesPath = Thread.currentThread().getContextClassLoader().getResource(src).getPath();
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(propertiesPath));
        } catch (IOException e) {
            logger.debug("Couldn't load properties source");
        }
        return properties;
    }

}
