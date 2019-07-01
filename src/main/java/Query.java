public class Query {
    public static final String INSERT_FEED = "insert into feeds(title, url) values (:title, :url)";
    public static final String GET_ALL_FEEDS = "SELECT title, url FROM feeds";
}
