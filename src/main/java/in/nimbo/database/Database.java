package in.nimbo.database;

import in.nimbo.model.Feed;
import in.nimbo.model.Report;
import org.apache.log4j.Logger;
import org.sql2o.Connection;
import org.sql2o.Query;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;


public class Database {
    private static Database ourInstance;
    private static Logger logger = Logger.getLogger(Database.class);
    private Sql2o sql2o;


    private Database(DataSource dataSource) {
        sql2o = new Sql2o(dataSource);
    }

    /**
     * Instance Of database Class - Pattern Singleton
     *
     * @return Database
     */
    public static Database getInstance() {
        if (ourInstance == null) {
            ourInstance = new Database(HikariCPDataSource.getDataSource());
        }
        return ourInstance;
    }

    public void executeQuery(String queryString) {
        try (Connection con = sql2o.open();
             Query query = con.createQuery(queryString)) {
            query.executeUpdate();
        } catch (Sql2oException e) {
            logger.error(e.getMessage());
        }
    }

    public void insertFeed(Feed feed) {
        try (Connection con = sql2o.open();
             Query query = con.createQuery(QueryStatement.INSERT_FEED)) {
            query.addParameter("title", feed.getTitle())
                    .addParameter("url", feed.getUrl())
                    .executeUpdate();
        } catch (Sql2oException e) {
            logger.error(e.getMessage());
        }
    }

    public void removeFeedWithReports(int feedId) {
        try (Connection con = sql2o.open()) {
            try (Query query = con.createQuery(QueryStatement.REMOVE_FEED)) {
                query.addParameter("id", feedId)
                        .executeUpdate();
            }
            try (Query query = con.createQuery(QueryStatement.REMOVE_FEEDS_REPORTS)) {
                query.addParameter("feedId", feedId)
                        .executeUpdate();
            }
        } catch (Sql2oException e) {
            logger.error(e.getMessage());
        }
    }

    public List<Feed> getAllFeeds() {
        try (Connection con = sql2o.open();
             Query query = con.createQuery(QueryStatement.GET_ALL_FEEDS)) {
            return query.executeAndFetch(Feed.class);
        } catch (Sql2oException e) {
            logger.error(e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<Report> getSimilarReports(String link) {
        try (Connection con = sql2o.open();
             Query query = con.createQuery(QueryStatement.GET_SIMILAR_REPORTS)) {
            return query.addParameter("link", link)
                    .executeAndFetch(Report.class);
        } catch (Sql2oException e) {
            logger.error(e.getMessage());
            return new ArrayList<>();
        }
    }

    public void insertReport(Report report) {
        try (Connection con = sql2o.open();
             Query query = con.createQuery(QueryStatement.INSERT_REPORT)) {
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
             Query query = con.createQuery(QueryStatement.GET_ALL_REPORTS)) {
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
             Query query = con.createQuery(searchQuery.generateQuery())) {
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
