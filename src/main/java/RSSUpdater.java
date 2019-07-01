import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;

import java.io.IOException;
import java.net.URL;

public class RSSUpdater extends Thread {


    @Override
    public void run() {
        while (true) {
            DB.getInstance().getAllFeeds().forEach(RSSUpdater::fetchReportsOfRSS);
            try {
                sleep(60000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static void fetchReportsOfRSS(Feed feed) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    SyndFeed rssFeed = new SyndFeedInput().build(
                            new XmlReader(new URL(feed.getUrl())));
                    rssFeed.getEntries().forEach(this::addReportToDatabase);
                } catch (FeedException | IOException e) {
                    e.printStackTrace();
                }
            }

            private void addReportToDatabase(SyndEntry report) {
                if (DB.getInstance().reportExists(report)) {
                    Report newReport = new Report(feed.getId(), report.getTitle(), report.getLink());
                    try {
                        newReport.setPubDate(report.getPublishedDate());
                    } catch (NullPointerException e) {

                    }
                    try {
                        newReport.setDescription(report.getDescription().getValue());
                    } catch (NullPointerException e) {

                    }
                    DB.getInstance().insertReport(newReport);
                }
            }
        }).start();
    }
}
