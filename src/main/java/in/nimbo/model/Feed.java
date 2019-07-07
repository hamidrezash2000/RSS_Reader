package in.nimbo.model;

import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.URL;

public class Feed {
    private static Logger logger = Logger.getLogger(Feed.class);
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

    /**
     * Pass a url to function and it returns the title of the rss feed.
     *
     * @param url to search for title
     * @return title in String
     */
    public static String getTitleOfRSSFeed(String url) {
        String title = "RSS in.nimbo.model.Feed Title";
        try {
            SyndFeed rssFeed = new SyndFeedInput().build(new XmlReader(new URL(url)));
            title = rssFeed.getTitle();
        } catch (FeedException | IOException e) {
            logger.info("Couldn't find title of RSS feed");
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

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Feed &&
                ((Feed) obj).getTitle().equals(title) &&
                ((Feed) obj).getUrl().equals(url);
    }
}
