package in.nimbo.database;

public class Query {
    public static final String INSERT_FEED = "insert into feeds(title, url) values (:title, :url)";
    public static final String REMOVE_FEED = "DELETE FROM feeds WHERE id=:id";
    public static final String REMOVE_FEEDS_REPORTS = "DELETE FROM reports WHERE feedId=:feedId";
    public static final String INSERT_REPORT = "insert into reports(feedId, title, link, pubDate, description) values (:feedId, :title, :link, :pubDate, :description)";
    public static final String GET_ALL_FEEDS = "SELECT id, title, url FROM feeds";
    public static final String GET_ALL_REPORTS = "SELECT feedId, title, link, description, pubDate FROM reports";
    public static final String GET_SIMILAR_REPORTS = "SELECT feedId, title, link FROM reports WHERE link = :link";
}
