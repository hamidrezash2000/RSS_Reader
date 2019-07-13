package in.nimbo;

import in.nimbo.database.Database;
import in.nimbo.model.Feed;
import in.nimbo.model.Report;
import manifold.ext.api.Jailbreak;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static junit.framework.TestCase.*;

public class RssUpdaterTest {
    private static final String createFeedTableQuery = "CREATE TABLE feeds (id INTEGER PRIMARY KEY AUTO_INCREMENT, title TEXT, url TEXT)";
    private static final String createReportTableQuery = "CREATE TABLE reports (id INTEGER PRIMARY KEY AUTO_INCREMENT, link TEXT , title TEXT , pubDate DATETIME, description TEXT , feedId INTEGER NOT NULL, FOREIGN KEY (feedId) REFERENCES feeds(id) ON DELETE CASCADE)";
    private static final String clearReportsTableQuery = "DELETE FROM reports";
    private static final String clearFeedsTableQuery = "DELETE FROM feeds";
    private static final Database database = Database.getInstance();

    @BeforeClass
    public static void createTables() {
        database.executeQuery(createFeedTableQuery);
        database.executeQuery(createReportTableQuery);
    }

    @AfterClass
    public static void deleteTables() {
        database.executeQuery("DROP TABLE feeds");
        database.executeQuery("DROP TABLE reports");
    }

    @Before
    public void clearTables() {
        database.executeQuery(clearFeedsTableQuery);
        database.executeQuery(clearReportsTableQuery);
    }

    @Test
    public void RssFetcherTest() {
        final String url = Thread.currentThread().getContextClassLoader().getResource("fetcher-test.xml").toString();
        database.insertFeed(new Feed(url));
        Feed feed = database.getAllFeeds().get(0);
        new RssFetcher(database, feed, new RssUpdater(database)).run();
        List<Report> fetchedReports = database.getAllReports();
        Report report1 = new Report(feed.getId(), "Title Test 1", "http://test1.test");
        Report report2 = new Report(feed.getId(), "Title Test 2", "http://test2.test");
        assertTrue(fetchedReports.contains(report1) && fetchedReports.contains(report2));
    }

    @Test
    public void handleInvalidLinksTest() {
        final String url = "https://fake.fake";
        database.insertFeed(new Feed(url));
        Feed feed = database.getAllFeeds().get(0);
        RssUpdater rssUpdater = new RssUpdater(database);
        @Jailbreak RssFetcher rssFetcher = new RssFetcher(database, feed, rssUpdater);
        for (int i = 0; i < 4; i++) {
            rssFetcher.run();
        }
        try {
            Thread.sleep(120000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertTrue(rssUpdater.invalidLinksCache.containsKey(feed.getId())
                && rssUpdater.invalidLinksCache.get(feed.getId()) == 4);
        rssUpdater.handleInvalidLinks();
        assertFalse(database.getAllFeeds().contains(feed));
    }
}
