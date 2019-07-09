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
        Report report1 = new Report(feed.getId(), "850 میلیارد تومان میزان مطالبات تامین اجتماعی در آذربایجان شرقی/ کارخانه های تولیدی واگذار شده بیشترین بدهی را دارند", "https://farsnews.com/news/13980418000619/50-%D9%85%DB%8C%D9%84%DB%8C%D8%A7%D8%B1%D8%AF-%D8%AA%D9%88%D9%85%D8%A7%D9%86-%D9%85%DB%8C%D8%B2%D8%A7%D9%86-%D9%85%D8%B7%D8%A7%D9%84%D8%A8%D8%A7%D8%AA-%D8%AA%D8%A7%D9%85%DB%8C%D9%86-%D8%A7%D8%AC%D8%AA%D9%85%D8%A7%D8%B9%DB%8C-%D8%AF%D8%B1-%D8%A2%D8%B0%D8%B1%D8%A8%D8%A7%DB%8C%D8%AC%D8%A7%D9%86-%D8%B4%D8%B1%D9%82%DB%8C-");
        Report report2 = new Report(feed.getId(), "پلمپ شیرینی فروشی دلیل برکناری مدیر تعزیرات کهگیلویه و بویراحمد نیست", "https://farsnews.com/news/13980418000614/%D9%BE%D9%84%D9%85%D9%BE-%D8%B4%DB%8C%D8%B1%DB%8C%D9%86%DB%8C-%D9%81%D8%B1%D9%88%D8%B4%DB%8C-%D8%AF%D9%84%DB%8C%D9%84-%D8%A8%D8%B1%DA%A9%D9%86%D8%A7%D8%B1%DB%8C-%D9%85%D8%AF%DB%8C%D8%B1-%D8%AA%D8%B9%D8%B2%DB%8C%D8%B1%D8%A7%D8%AA-%DA%A9%D9%87%DA%AF%DB%8C%D9%84%D9%88%DB%8C%D9%87-%D9%88-%D8%A8%D9%88%DB%8C%D8%B1%D8%A7%D8%AD%D9%85%D8%AF-%D9%86%DB%8C%D8%B3%D8%AA");
        assertEquals(Arrays.asList(report1, report2), fetchedReports);
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
        assertTrue(rssUpdater.invalidLinksCache.containsKey(feed.getId())
                && rssUpdater.invalidLinksCache.get(feed.getId()) == 4);
        rssUpdater.handleInvalidLinks();
        assertFalse(database.getAllFeeds().contains(feed));
    }
}
