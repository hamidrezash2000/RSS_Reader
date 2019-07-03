import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MainTest {
    @Test
    public void getTitleOfRSSFeedTest() {
        final String url = "https://www.tasnimnews.com/fa/rss/feed/0/7";
        final String title = "آخرین اخبار, اخبار روز";
        String titleOfRSSFeed = Feed.getTitleOfRSSFeed(url);
        assertEquals(title, titleOfRSSFeed);
    }

    @Test
    public void getPropertyTest() {
        PropertiesManager.getProperty(PropertiesManager.DATABASSE).put("property-test-key", "property-test-value");
        assertTrue(PropertiesManager.getProperty(PropertiesManager.DATABASSE).getProperty("property-test-key").equals("property-test-value"));
//        PropertiesManager.getProperty(PropertiesManager.DATABASSE).remove("property-test-key");
    }
}
