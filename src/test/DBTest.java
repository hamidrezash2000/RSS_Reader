import org.junit.*;

import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.List;

import static junit.framework.TestCase.*;


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
    public void getSimilarReportsTest() {
        Report report1 = new Report(1, "Test1 Similarity", "http://Test1.com");
        Report report2 = new Report(2, "Test1 Similarity", "http://Test1.com");
        Report report3 = new Report(3, "Test1 Similarity", "http://Test1.com");
        Report report4 = new Report(4, "Test1 Similar", "http://Test1.com");
        DB.getInstanceForTest().insertReport(report1);
        DB.getInstanceForTest().insertReport(report2);
        DB.getInstanceForTest().insertReport(report3);
        DB.getInstanceForTest().insertReport(report4);
        List<Report> similarReports = DB.getInstanceForTest().getSimilarReports(report1.getTitle(), report1.getLink());
        assertEquals(similarReports, Arrays.asList(report1, report2, report3));
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

    @Test
    public void getSimilarReportsTest() {
        Report duplicateReport1 = new Report(1, "Test Duplicate Report Title", "http://TestURL.URL");
        Report duplicateReport2 = new Report(1, "Test Duplicate Report Title", "http://TestURL.URL");
        DB.getInstanceForTest().insertReport(duplicateReport1);
        DB.getInstanceForTest().insertReport(duplicateReport2);
        final List<Report> similarReports = DB.getInstanceForTest().getSimilarReports("Test Duplicate Report Title", "http://TestURL.URL");
        assertTrue(similarReports.containsAll(Arrays.asList(duplicateReport1, duplicateReport2)) && similarReports.size() == 2);
    }

    @Test
    public void searchReportsPubDateTest() {
        Report reportToSearch1 = new Report(1, "Test Duplicate Report Title 1", "http://TestURL1.URL",
                new GregorianCalendar(2000, 1 , 1).getTime(), "Description 1");
        Report reportToSearch2 = new Report(1, "Test Duplicate Report Title 2", "http://TestURL2.URL",
                new GregorianCalendar(2005, 1 , 1).getTime(), "Description 2");
        Report reportToSearch3 = new Report(1, "Test Duplicate Report Title 3", "http://TestURL3.URL",
                new GregorianCalendar(2003, 1 , 1).getTime(), "Description 3");
        DB.getInstanceForTest().insertReport(reportToSearch1);
        DB.getInstanceForTest().insertReport(reportToSearch2);
        DB.getInstanceForTest().insertReport(reportToSearch3);
        final List<Report> searchedReports = DB.getInstanceForTest()
                .searchReports(1, "Test Duplicate Report Title",
                        new GregorianCalendar(2000, 1 , 1).getTime(),
                        new GregorianCalendar(2004, 1 , 1).getTime());

        assertTrue(searchedReports.containsAll(Arrays.asList(reportToSearch1, reportToSearch3)) && !searchedReports.contains(reportToSearch2));
    }

    @AfterClass
    public static void deleteTables() {
        DB.getInstanceForTest().executeQueryOnTest("DROP TABLE feeds");
        DB.getInstanceForTest().executeQueryOnTest("DROP TABLE reports");
    }
}
