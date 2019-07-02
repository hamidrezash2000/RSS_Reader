import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import de.l3s.boilerpipe.BoilerpipeProcessingException;
import de.l3s.boilerpipe.extractors.ArticleExtractor;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class RSSUpdater extends Thread {
    private static Logger logger = Logger.getLogger(RSSUpdater.class);

    @Override
    public void run() {
        while (true) {
            DB.getInstance().getAllFeeds().forEach(RSSUpdater::fetchReportsOfRSS);
            try {
                sleep(60000);
            } catch (InterruptedException e) {
                logger.error(e.getMessage());
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
                    logger.error(e.getMessage());
                }
            }

            private void addReportToDatabase(SyndEntry report) {
                if (DB.getInstance().reportExists(report)) {
                    Report newReport = new Report(feed.getId(), report.getTitle(), report.getLink());
                    try {
                        newReport.setPubDate(report.getPublishedDate());
                    } catch (NullPointerException e) {
                        logger.info("Couldn't find pubDate of report");
                    }
                    try {
                        String fullTextOfReport = ArticleExtractor.INSTANCE.getText(new URL(report.getLink()));
                        newReport.setDescription(fullTextOfReport);
                    } catch (NullPointerException e) {
                        logger.info("Couldn't find description of report");
                    } catch (MalformedURLException | BoilerpipeProcessingException e) {
                        logger.error(e.getMessage());
                    }
                    DB.getInstance().insertReport(newReport);
                }
            }
        }).start();
    }
}
