import org.junit.*;

import static junit.framework.TestCase.assertFalse;

import java.util.Arrays;
import java.util.List;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

public class DBTest {

    static final String createFeedTableQuery = "CREATE TABLE feeds (id INTEGER PRIMARY KEY AUTO_INCREMENT, title TEXT, url TEXT)";
    static final String createReportTableQuery = "CREATE TABLE reports (id INTEGER PRIMARY KEY AUTO_INCREMENT, link TEXT , title TEXT , pubDate DATETIME, description TEXT , feedId INTEGER NOT NULL)";

    @BeforeClass
    public static void createTables() {
        ;
        DB.getInstanceForTest().executeQueryOnTest(createFeedTableQuery);
        DB.getInstanceForTest().executeQueryOnTest(createReportTableQuery);
    }

    @Test
    public void insertFeedTest() {
        Feed feed = new Feed("فارسی", "http://TestURL.URL");
        DB.getInstanceForTest().insertFeed(feed);
        assertTrue(DB.getInstanceForTest().getAllFeeds().contains(feed));
    }

    @Test
    public void insertReportTest() {
        Report report = new Report(1, "Test Report Title", "http://TestURL.URL");
        DB.getInstanceForTest().insertReport(report);
        assertTrue(DB.getInstanceForTest().getAllReports().contains(report));
    }

    @Test
    public void reportExistsTest() {
        Report report1 = new Report(1, "Test Similarity", "http://Test.com");
        Report report2 = new Report(2, "Test Similarity", "http://Test.com");
        Report report3 = new Report(3, "Test Similar", "http://Test.com");
        DB.getInstanceForTest().insertReport(report1);
        assertFalse(DB.getInstanceForTest().reportNotExists(report2.getTitle(), report2.getLink()));
        assertTrue(DB.getInstanceForTest().reportNotExists(report3.getTitle(), report3.getLink()));
    }

    @Test
    public void getAllFeedsTest() {
        Feed feed1 = new Feed("Test Title 1", "http://TestURL1.URL");
        Feed feed2 = new Feed("Test Title 2", "http://TestURL2.URL");
        Feed feed3 = new Feed("Test Title 3", "http://TestURL3.URL");
        DB.getInstanceForTest().insertFeed(feed1);
        DB.getInstanceForTest().insertFeed(feed2);
        DB.getInstanceForTest().insertFeed(feed3);
        final List<Feed> allReports = DB.getInstanceForTest().getAllFeeds();
        assertTrue(allReports.containsAll(Arrays.asList(feed1, feed2, feed3)));
    }

    @AfterClass
    public static void deleteTables() {
        DB.getInstanceForTest().executeQueryOnTest("DROP TABLE feeds");
        DB.getInstanceForTest().executeQueryOnTest("DROP TABLE reports");
    }
}
