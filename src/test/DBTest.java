import org.junit.Test;
import static junit.framework.TestCase.assertTrue;

public class DBTest {
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
}
