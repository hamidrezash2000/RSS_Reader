import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MainTest {
    @Test
    public void getTitleOfRSSFeedTest() {
        final String url = "https://www.tasnimnews.com/fa/rss/feed/0/7";
        final String title = "آخرین اخبار, اخبار روز";
        String titleOfRSSFeed = Feed.getTitleOfRSSFeed(url);
        assertEquals(title, titleOfRSSFeed);
    }
}
