import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;

import java.io.IOException;
import java.net.URL;

public class Feed {
    private int id;
    private String title;
    private String url;

    public Feed(String title, String url) {
        this.title = title;
        this.url = url;
    }

    public Feed(String url) {
        this(getTitleOfRSSFeed(url), url);
    }

    private static String getTitleOfRSSFeed(String url) {
        String title = "RSS Feed Title";
        try {
            SyndFeed rssFeed = new SyndFeedInput().build(new XmlReader(new URL(url)));
            title = rssFeed.getTitle();
        } catch (FeedException | IOException e) {
            e.printStackTrace();
        }
        return title;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
