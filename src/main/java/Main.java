import java.net.MalformedURLException;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;

import java.io.IOException;
import java.net.URL;
import java.util.function.Consumer;

public class Main {

    public static void main(String[] args) {
        DB.getInstance().getAllFeeds().forEach(feed -> {
            String feedUrl = feed.getUrl();
            SyndFeed rssFeed = null;
            try {
                rssFeed = new SyndFeedInput().build(new XmlReader(new URL(feedUrl)));
            } catch (IOException | FeedException e) {
                e.printStackTrace();
            }
            System.out.println("::::" + feed.getTitle());
            rssFeed.getEntries().forEach(report -> {
                System.out.println(report.getTitle());
            });
        });
//        try {
//            DB.getInstance().insertFeed("FarsNews", new URL("https://www.farsnews.com/rss"));
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        }
    }
}
