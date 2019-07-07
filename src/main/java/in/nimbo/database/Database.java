package in.nimbo.database;

import in.nimbo.model.Feed;
import in.nimbo.model.Report;
import in.nimbo.util.PropertiesManager;
import org.apache.log4j.Logger;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Database {
    private static Database ourInstance;
    private static Logger logger = Logger.getLogger(Database.class);
    private Sql2o sql2o;

    /**
     * set sql2o sql connection
     *
     * @param connectionURL
     * @param username
     * @param password
     */
    private Database(String connectionURL, String username, String password) {
        sql2o = new Sql2o(connectionURL, username, password);
    }

    /**
     * Instance Of in.nimbo.database.in.nimbo.database Class
     *
     * @return Mysql in.nimbo.database.in.nimbo.database
     */
    public static Database getInstance() {
        if (ourInstance == null) {
            Properties properties = PropertiesManager.database;
            ourInstance = new Database(
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

    public void executeQuery(String queryString) {
        try (Connection con = sql2o.open();
             org.sql2o.Query query = con.createQuery(queryString)) {
            query.executeUpdate();
        } catch (Sql2oException e) {
            logger.error(e.getMessage());
        }
    }

    public void insertFeed(Feed feed) {
        try (Connection con = sql2o.open();
             org.sql2o.Query query = con.createQuery(Query.INSERT_FEED)) {
            query.addParameter("title", feed.getTitle())
                    .addParameter("url", feed.getUrl())
                    .executeUpdate();
        } catch (Sql2oException e) {
            logger.error(e.getMessage());
        }
    }

    public void removeFeedWithReports(int feedId) {
        try (Connection con = sql2o.open()) {
            try (org.sql2o.Query query = con.createQuery(Query.REMOVE_FEED)) {
                query.addParameter("id", feedId)
                        .executeUpdate();
            }
            try (org.sql2o.Query query = con.createQuery(Query.REMOVE_FEEDS_REPORTS)) {
                query.addParameter("feedId", feedId)
                        .executeUpdate();
            }
        } catch (Sql2oException e) {
            logger.error(e.getMessage());
        }
    }

    public List<Feed> getAllFeeds() {
        try (Connection con = sql2o.open();
             org.sql2o.Query query = con.createQuery(Query.GET_ALL_FEEDS)) {
            return query.executeAndFetch(Feed.class);
        } catch (Sql2oException e) {
            logger.error(e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<Report> getSimilarReports(String link) {
        try (Connection con = sql2o.open();
             org.sql2o.Query query = con.createQuery(Query.GET_SIMILAR_REPORTS)) {
                return query.addParameter("link", link)
                        .executeAndFetch(Report.class);
        } catch (Sql2oException e) {
            logger.error(e.getMessage());
            return new ArrayList<>();
        }
    }

    public void insertReport(Report report) {
        try (Connection con = sql2o.open();
             org.sql2o.Query query = con.createQuery(Query.INSERT_REPORT)) {
                query.addParameter("title", report.getTitle())
                        .addParameter("link", report.getLink())
                        .addParameter("pubDate", report.getPubDate())
                        .addParameter("description", report.getDescription())
                        .addParameter("feedId", report.getFeedId())
                        .executeUpdate();
        } catch (Sql2oException e) {
            logger.error(e.getMessage());
        }
    }

    public List<Report> getAllReports() {
        try (Connection con = sql2o.open();
             org.sql2o.Query query = con.createQuery(Query.GET_ALL_REPORTS)) {
            return query.executeAndFetch(Report.class);
        } catch (Sql2oException e) {
            logger.error(e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Search in in.nimbo.database With Parameters :
     *
     * @param searchQuery : in.nimbo.database.SearchQuery Object
     * @return List Of Reports
     */
    public List<Report> searchReports(SearchQuery searchQuery) {
        try (Connection con = sql2o.open();
             org.sql2o.Query query = con.createQuery(searchQuery.generateQuery())) {
            return query.executeAndFetch(Report.class);
        } catch (Sql2oException e) {
            logger.error(e.getMessage());
            return new ArrayList<>();
        }
    }

    public boolean reportNotExists(String link) {
        return getSimilarReports(link).size() == 0;
    }

}
