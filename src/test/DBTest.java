import org.junit.*;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;

public class DBTest {

    static final String createFeedTableQuery = "CREATE TABLE feeds (id INTEGER PRIMARY KEY AUTO_INCREMENT, title TEXT, url TEXT)";
    static final String createReportTableQuery = "CREATE TABLE reports (id INTEGER PRIMARY KEY AUTO_INCREMENT, link TEXT , title TEXT , pubDate DATETIME, description TEXT , feedId INTEGER NOT NULL)";

    @BeforeClass
    public static void createTables () {;
        DB.getInstanceForTest().executeQueryOnTest(createFeedTableQuery);
        DB.getInstanceForTest().executeQueryOnTest(createReportTableQuery);
    }

    @Test
    public void insertFeedTest() {
        Feed feed = new Feed("Test Title", "http://TestURL.URL");
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

    @AfterClass
    public static void deleteTables() {
        DB.getInstanceForTest().executeQueryOnTest("DROP TABLE feeds");
        DB.getInstanceForTest().executeQueryOnTest("DROP TABLE reports");
    }
}
