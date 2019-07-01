public class Query {
    public static final String INSERT_FEED = "insert into feeds(title, url) values (:title, :url)";
    public static final String INSERT_REPORT = "insert into reports(feedId, title, link, pubDate, description) values (:feedId, :title, :link, :pubDate, :description)";
    public static final String GET_ALL_FEEDS = "SELECT id, title, url FROM feeds";
    public static final String GET_ALL_REPORTS = "SELECT feedId, title, link FROM reports";
    public static final String GET_SIMILAR_REPORTS = "SELECT feedId, title, link FROM reports WHERE title = :title1223 AND link = :link";
    public static final String SEARCH_IN_REPORTS = "SELECT feedId, title, link FROM reports WHERE title LIKE :wildcard";
}
