package database;

import model.Feed;
import model.Report;
import util.PropertiesManager;
import org.apache.log4j.Logger;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

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
     * Instance Of database.database Class
     *
     * @return Mysql database.database
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

    public void removeFeedWithReports(int feedId) {
        try (Connection con = sql2o.open()) {
            con.createQuery(Query.REMOVE_FEED)
                    .addParameter("id", feedId)
                    .executeUpdate()
                    .createQuery(Query.REMOVE_FEEDS_REPORTS)
                    .addParameter("feedId", feedId)
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
     * Search in database With Parameters :
     *
     * @param searchQuery : database.SearchQuery Object
     * @return List Of Reports
     */
    public List<Report> searchReports(SearchQuery searchQuery) {
        try (Connection con = sql2o.open()) {
            return con.createQuery(searchQuery.generateQuery())
                    .executeAndFetch(Report.class);
        }
    }

    public boolean reportNotExists(String title, String link) {
        return DB.getInstance().getSimilarReports(title, link).size() == 0;
    }

}
