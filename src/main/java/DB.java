import org.apache.log4j.Logger;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;

public class DB {
    private static DB ourInstance;
    private static Logger logger = Logger.getLogger(DB.class);
    private Sql2o sql2o;

    /**
     * set sql2o sql connection
     *
     * @param connectionURL
     * @param username
     * @param password
     */
    private DB(String connectionURL, String username, String password) {
        sql2o = new Sql2o(connectionURL, username, password);
    }

    /**
     * Instance Of DB Class
     *
     * @return Mysql DB
     */
    public static DB getInstance() {
        if (ourInstance == null) {
            Properties properties = PropertiesManager.getProperty(PropertiesManager.DATABASSE);
            ourInstance = new DB(
                    String.format("jdbc:%s/%s?useUnicode=true&characterEncoding=UTF-8",
                            properties.getProperty("address"),
                            properties.getProperty("database")
                    ),
                    properties.getProperty("username"),
                    properties.getProperty("password")
            );
        }
        return ourInstance;
    }

    public void executeQueryOnTest(String query) {
        try (Connection con = sql2o.open()) {
            con.createQuery(query).executeUpdate();
        }
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

    public boolean reportNotExists(String title, String link) {
        return DB.getInstance().getSimilarReports(title, link).size() == 0;
    }

}
