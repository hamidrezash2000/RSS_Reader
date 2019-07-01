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
            System.out.println("::::" + feed.getTitle() + feed.getId());
            rssFeed.getEntries().forEach(reportEntry -> {
                Report report = new Report(feed.getId(),
                        reportEntry.getTitle(),
                        reportEntry.getLink());
                report.setPubDate(reportEntry.getPublishedDate());
                report.setDescription(reportEntry.getDescription().getValue());
                DB.getInstance().insertReport(report);
            });
        });
//        try {
//            DB.getInstance().insertFeed("FarsNews", new URL("https://www.farsnews.com/rss"));
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        }
    }
}
