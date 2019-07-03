import org.junit.Test;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

public class MainTest {
    @Test
    public void getTitleOfRSSFeedTest() {
        final String url = Thread.currentThread().getContextClassLoader().getResource("feed-title-test.xml").toString();
        final String title = "تیتر تست خبر";
        String titleOfRSSFeed = Feed.getTitleOfRSSFeed(url);
        assertEquals(title, titleOfRSSFeed);
    }

    @Test
    public void getPropertyTest() {
        assertTrue(PropertiesManager.getProperty("test-property.properties")
                .getProperty("property-test-key").equals("property-test-value"));
    }
}
