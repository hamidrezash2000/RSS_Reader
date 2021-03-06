package in.nimbo;

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
    public void removeFeedWithReportsTest () {
        Feed feed = new Feed("تیتر تست فارسی", "http://TestURL.URL");
        database.insertFeed(feed);
        int feedId = database.getAllFeeds().get(0).getId();
        Report report1 = new Report(feedId, "Test Duplicate Report 1", "http://Test1.com");
        Report report2 = new Report(feedId, "Test Duplicate Report 2", "http://Test2.com");
        database.removeFeedWithReports(feedId);
        assertTrue(!database.getAllReports().contains(report1) &&
                !database.getAllReports().contains(report2) &&
                !database.getAllFeeds().contains(feed));
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
        assertTrue(database.reportExists(similarReport.getLink()));
        assertFalse(database.reportExists(differentReport.getLink()));
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
    public void searchReportsByPubDateTest() {
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

    @Test
    public void searchReportsByFeedIdTest() {
        database.insertFeed(new Feed("تیتر تست فارسی 1", "http://TestURL1.URL"));
        database.insertFeed(new Feed("تیتر تست فارسی 2", "http://TestURL2.URL"));
        List<Feed> allFeeds = database.getAllFeeds();
        int feedId1 = allFeeds.get(0).getId();
        int feedId2 = allFeeds.get(1).getId();
        Report sameReport1 = new Report(feedId1, "Test Duplicate Report Title 1", "http://TestURL1.URL");
        Report sameReport2 = new Report(feedId1, "Test Duplicate Report Title 2", "http://TestURL2.URL");
        Report differentReport = new Report(feedId2, "Test Duplicate Report Title 3", "http://TestURL3.URL");
        database.insertReport(sameReport1);
        database.insertReport(sameReport2);
        database.insertReport(differentReport);
        SearchQuery searchQuery = new SearchQuery();
        searchQuery.setFeedId(feedId1);
        final List<Report> searchedReports = database.searchReports(searchQuery);
        assertTrue(searchedReports.containsAll(Arrays.asList(sameReport1, sameReport2))
                && !searchedReports.contains(differentReport));
    }

    @Test
    public void searchReportsByTitle() {
        database.insertFeed(new Feed("Test Title", "http://TestURL.URL"));
        int feedId = database.getAllFeeds().get(0).getId();
        Report sameReport1 = new Report(feedId, "Test Title Same 1", "http://TestURL1.URL");
        Report sameReport2 = new Report(feedId, "Test Title Same 2", "http://TestURL2.URL");
        Report differentReport = new Report(feedId, "Test Title Different", "http://TestURL3.URL");
        database.insertReport(sameReport1);
        database.insertReport(sameReport2);
        database.insertReport(differentReport);
        SearchQuery searchQuery = new SearchQuery();
        searchQuery.setTitle("Test Title Same");
        final List<Report> searchedReports = database.searchReports(searchQuery);
        assertTrue(searchedReports.containsAll(Arrays.asList(sameReport1, sameReport2))
                && !searchedReports.contains(differentReport));
    }

    @Test
    public void searchReportsByDescription() {
        database.insertFeed(new Feed("Test Title", "http://TestURL.URL"));
        int feedId = database.getAllFeeds().get(0).getId();
        Report sameReport1 = new Report(feedId, "Test Title Same 1", "http://TestURL1.URL");
        sameReport1.setDescription("Test Desc Same 1");
        Report sameReport2 = new Report(feedId, "Test Title Same 2", "http://TestURL2.URL");
        sameReport2.setDescription("Test Desc Same 1");
        Report differentReport = new Report(feedId, "Test Title Different", "http://TestURL3.URL");
        differentReport.setDescription("Test Desc Different");
        database.insertReport(sameReport1);
        database.insertReport(sameReport2);
        database.insertReport(differentReport);
        SearchQuery searchQuery = new SearchQuery();
        searchQuery.setDescription("Test Desc Same");
        final List<Report> searchedReports = database.searchReports(searchQuery);
        assertTrue(searchedReports.containsAll(Arrays.asList(sameReport1, sameReport2))
                && !searchedReports.contains(differentReport));
    }
}
