import com.rometools.rome.feed.synd.SyndEntry;
import org.sql2o.Connection;
import org.sql2o.Sql2o;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

public class DB {
    private static DB ourInstance = new DB();
    private Sql2o sql2o;

    private DB() {
        Properties properties = getProperty("database.properties");
        sql2o = new Sql2o(
                String.format("jdbc:mysql://%s:%s/rss?useUnicode=true&characterEncoding=UTF-8",
                        properties.getProperty("ip"),
                        properties.getProperty("port")
                ),
                properties.getProperty("username"),
                properties.getProperty("password"));
    }

    public static DB getInstance() {
        return ourInstance;
    }

    private static Properties getProperty(String src) {
        String propertiesPath = Thread.currentThread().getContextClassLoader().getResource(src).getPath();
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(propertiesPath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
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
                    .addParameter("title1223", title)
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

    public List<Report> searchReports(String toFind) {
        try (Connection con = sql2o.open()) {
            String searchQuery = String.format("SELECT feedId, title, link FROM reports WHERE title LIKE '%%%s%%'", toFind);
            return con.createQuery(searchQuery)
                    .executeAndFetch(Report.class);
        }
    }

    public boolean reportExists(SyndEntry report) {
        return DB.getInstance().getSimilarReports(report.getTitle(), report.getLink()).size() == 0;
    }

}
