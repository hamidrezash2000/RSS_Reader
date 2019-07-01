
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import java.net.URL;
import java.util.List;

public class DB {
    private Sql2o sql2o;
    private static DB ourInstance = new DB();

    public static DB getInstance() {
        return ourInstance;
    }

    private DB() {
        sql2o = new Sql2o("jdbc:mysql://127.0.0.1:3306/rss", "username", "12345678");
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
        try(Connection con = sql2o.open()) {
            return con.createQuery(Query.GET_ALL_FEEDS)
                    .executeAndFetch(Feed.class);
        }
    }

    public void insertReport(Report report) {
        try (Connection con = sql2o.open()) {
            con.createQuery(Query.INSERT_FEED)
                    .addParameter("feedId", report.getFeedId())
                    .addParameter("title", report.getTitle())
                    .addParameter("link", report.getLink())
                    .addParameter("pubDate", report.getPubDate())
                    .addParameter("description", report.getDescription())
                    .executeUpdate();
        }
    }



}
