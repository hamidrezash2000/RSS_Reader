

import in.nimbo.database.Database;
import in.nimbo.database.SearchQuery;
import in.nimbo.model.Feed;
import in.nimbo.model.Report;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.List;

import static junit.framework.TestCase.*;


public class DatabaseTest {

    static final String createFeedTableQuery = "CREATE TABLE feeds (id INTEGER PRIMARY KEY AUTO_INCREMENT, title TEXT, url TEXT)";
    static final String createReportTableQuery = "CREATE TABLE reports (id INTEGER PRIMARY KEY AUTO_INCREMENT, link TEXT , title TEXT , pubDate DATETIME, description TEXT , feedId INTEGER NOT NULL, FOREIGN KEY (feedId) REFERENCES feeds(id) ON DELETE CASCADE)";
    static final String clearReportsTableQuery = "DELETE FROM reports";
    static final String clearFeedsTableQuery = "DELETE FROM feeds";
    static final Database database = Database.getInstance();

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
    public void insertFeedTest() {
        Feed feed = new Feed("تیتر تست تیتر تست فارسی", "http://TestURL.URL");
        database.insertFeed(feed);
        assertTrue(database.getAllFeeds().contains(feed));
    }

    @Test
    public void insertReportTest() {
        database.insertFeed(new Feed("تیتر تست تیتر تست فارسی", "http://TestURL.URL"));
        int feedId = database.getAllFeeds().get(0).getId();
        Report report = new Report(feedId, "Test Report Title", "http://TestURL.URL");
        database.insertReport(report);
        assertTrue(database.getAllReports().contains(report));
    }

    @Test
    public void reportExistsTest() {
        database.insertFeed(new Feed("تیتر تست فارسی", "http://TestURL.URL"));
        int feedId = database.getAllFeeds().get(0).getId();
        Report report = new Report(feedId, "Test Duplicate Report 1", "http://Test1.com");
        Report similarReport = new Report(feedId, "Test Duplicate Report 2", "http://Test1.com");
        Report differentReport = new Report(feedId, "Test Duplicate Report 3", "http://Test2.com");
        database.insertReport(report);
        assertFalse(database.reportNotExists(similarReport.getLink()));
        assertTrue(database.reportNotExists(differentReport.getLink()));
    }

    @Test
    public void getSimilarReportsTest() {
        database.insertFeed(new Feed("تیتر تست فارسی", "http://TestURL.URL"));
        int feedId = database.getAllFeeds().get(0).getId();
        Report similarReport1 = new Report(feedId, "Test1 Similarity", "http://Test1.com");
        Report similarReport2 = new Report(feedId, "Test2 Similarity", "http://Test1.com");
        Report differentReport = new Report(feedId, "Test3 Similarity", "http://Test2.com");
        database.insertReport(similarReport1);
        database.insertReport(similarReport2);
        database.insertReport(differentReport);
        List<Report> similarReports = database.getSimilarReports(similarReport1.getLink());
        assertEquals(similarReports, Arrays.asList(similarReport1, similarReport2));
    }

    @Test
    public void getAllFeedsTest() {
        Feed feed1 = new Feed("Test Title 1", "http://TestURL1.URL");
        Feed feed2 = new Feed("Test Title 2", "http://TestURL2.URL");
        Feed feed3 = new Feed("Test Title 3", "http://TestURL3.URL");
        database.insertFeed(feed1);
        database.insertFeed(feed2);
        database.insertFeed(feed3);
        final List<Feed> allFeeds = database.getAllFeeds();
        assertTrue(allFeeds.containsAll(Arrays.asList(feed1, feed2, feed3)));
    }

    @Test
    public void getAllReportsTest() {
        database.insertFeed(new Feed("تیتر تست فارسی", "http://TestURL.URL"));
        int feedId = database.getAllFeeds().get(0).getId();
        Report report1 = new Report(feedId, "Test Report Title 1", "http://TestURL1.URL");
        Report report2 = new Report(feedId, "Test Report Title 2", "http://TestURL2.URL");
        Report report3 = new Report(feedId, "Test Report Title 3", "http://TestURL3.URL");
        database.insertReport(report1);
        database.insertReport(report2);
        database.insertReport(report3);
        final List<Report> allReports = database.getAllReports();
        assertTrue(allReports.containsAll(Arrays.asList(report1, report2, report3)));
    }

    @Test
    public void searchReportsPubDateTest() {
        database.insertFeed(new Feed("تیتر تست فارسی", "http://TestURL.URL"));
        int feedId = database.getAllFeeds().get(0).getId();
        Report reportToSearch1 = new Report(feedId, "Test Duplicate Report Title 1", "http://TestURL1.URL",
                new GregorianCalendar(2000, 1, 1).getTime(), "Description 1");
        Report reportToSearch2 = new Report(feedId, "Test Duplicate Report Title 2", "http://TestURL2.URL",
                new GregorianCalendar(2005, 1, 1).getTime(), "Description 2");
        Report reportToSearch3 = new Report(feedId, "Test Duplicate Report Title 3", "http://TestURL3.URL",
                new GregorianCalendar(2003, 1, 1).getTime(), "Description 3");
        database.insertReport(reportToSearch1);
        database.insertReport(reportToSearch2);
        database.insertReport(reportToSearch3);
        SearchQuery searchQuery = new SearchQuery();
        searchQuery.setLowerAndUpperBound(
                new GregorianCalendar(2000, 1, 1).getTime(),
                new GregorianCalendar(2004, 1, 1).getTime());
        final List<Report> searchedReports = database.searchReports(searchQuery);
        assertTrue(searchedReports.containsAll(Arrays.asList(reportToSearch1, reportToSearch3))
                && !searchedReports.contains(reportToSearch2));
    }
}
