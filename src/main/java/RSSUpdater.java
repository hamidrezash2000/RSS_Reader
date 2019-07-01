import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class RSSUpdater extends Thread {
    @Override
    public void run() {
        DB.getInstance().getAllFeeds().forEach(feed -> {
            SyndFeed rssFeed = null;
            String feedUrl = feed.getUrl();
            try {
                rssFeed = new SyndFeedInput().build(new XmlReader(new URL(feedUrl)));
                rssFeed.getEntries().forEach(report -> {
                    if (DB.getInstance().getSimilarReports(report.getTitle(), report.getLink()).size() == 0) {

//                        Report newReport = new Report();
//                        DB.getInstance().insertReport();
                    }
                });
            } catch (FeedException | IOException e) {
                e.printStackTrace();
            }
        });
    }
}
