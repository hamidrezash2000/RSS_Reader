import org.junit.*;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

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
    public void getAllFeedsTest() {
        Feed feed1 = new Feed("Test Title 1", "http://TestURL1.URL");
        Feed feed2 = new Feed("Test Title 2", "http://TestURL2.URL");
        Feed feed3 = new Feed("Test Title 3", "http://TestURL3.URL");
        DB.getInstanceForTest().insertFeed(feed1);
        DB.getInstanceForTest().insertFeed(feed2);
        DB.getInstanceForTest().insertFeed(feed3);
        final List<Feed> allFeeds = DB.getInstanceForTest().getAllFeeds();
        assertTrue(allFeeds.containsAll(Arrays.asList(feed1, feed2, feed3)));
    }

    @Test
    public void getAllReportsTest() {
        Report report1 = new Report(1, "Test Report Title 1", "http://TestURL1.URL");
        Report report2 = new Report(2, "Test Report Title 2", "http://TestURL2.URL");
        Report report3 = new Report(3, "Test Report Title 3", "http://TestURL3.URL");
        DB.getInstanceForTest().insertReport(report1);
        DB.getInstanceForTest().insertReport(report2);
        DB.getInstanceForTest().insertReport(report3);
        final List<Report> allReports = DB.getInstanceForTest().getAllReports();
        assertTrue(allReports.containsAll(Arrays.asList(report1, report2, report3)));
    }

    @AfterClass
    public static void deleteTables() {
        DB.getInstanceForTest().executeQueryOnTest("DROP TABLE feeds");
        DB.getInstanceForTest().executeQueryOnTest("DROP TABLE reports");
    }
}
