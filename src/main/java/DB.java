
import com.rometools.rome.feed.synd.SyndEntry;
import org.sql2o.Connection;
import org.sql2o.Sql2o;


import java.util.List;

public class DB {
    private Sql2o sql2o;
    private static DB ourInstance = new DB();

    public static DB getInstance() {
        return ourInstance;
    }

    private DB() {
        sql2o = new Sql2o("jdbc:mysql://127.0.0.1:3306/rss?useUnicode=true&characterEncoding=UTF-8", "username", "12345678");
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
        try(Connection con = sql2o.open()) {
            return con.createQuery(Query.GET_ALL_REPORTS)
                    .executeAndFetch(Report.class);
        }
    }

    public boolean reportExists(SyndEntry report) {
        return DB.getInstance().getSimilarReports(report.getTitle(), report.getLink()).size() == 0;
    }

}