
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

    public void insertFeed(String title, URL url) {
        try (Connection con = sql2o.open()) {
            con.createQuery(Query.INSERT_FEED)
                    .addParameter("title", title)
                    .addParameter("url", url.toString())
                    .executeUpdate();
        }
    }

    public List<Feed> getAllFeeds() {
        try(Connection con = sql2o.open()) {
            return con.createQuery(Query.GET_ALL_FEEDS)
                    .executeAndFetch(Feed.class);
        }
    }




}
