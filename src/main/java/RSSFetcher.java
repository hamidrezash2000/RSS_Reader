import database.DB;
import model.Feed;
import model.Report;
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

public class RSSFetcher implements Runnable {

    private static Logger logger = Logger.getLogger(RSSUpdater.class);
    private Feed feed;

    public RSSFetcher(Feed feed) {
        this.feed = feed;
    }

    @Override
    public void run() {
        RSSUpdater.fetcherMetric.mark();
        try {
            SyndFeed rssFeed = new SyndFeedInput().build(
                    new XmlReader(new URL(feed.getUrl())));

            rssFeed.getEntries().forEach(this::addReportToDatabase);
        } catch (FeedException | IOException e) {
            logger.error(e.getMessage());
        }
    }

    private void addReportToDatabase(SyndEntry report) {
        if (DB.getInstance().reportNotExists(report.getTitle(), report.getLink())) {
            Report newReport = new Report(feed.getId(), report.getTitle(), report.getLink());
            setPubDate(report, newReport);
            extractMainContentOfReport(report, newReport);
            DB.getInstance().insertReport(newReport);
        }
    }

    private void setPubDate(SyndEntry report, Report newReport) {
        try {
            newReport.setPubDate(report.getPublishedDate());
        } catch (NullPointerException e) {
            logger.info("Couldn't find pubDate of report");
        }
    }

    private void extractMainContentOfReport(SyndEntry report, Report newReport) {
        try {
            String fullTextOfReport = ArticleExtractor.INSTANCE.getText(new URL(report.getLink()));
            newReport.setDescription(fullTextOfReport);
        } catch (NullPointerException e) {
            logger.info("Couldn't find description of report");
        } catch (MalformedURLException | BoilerpipeProcessingException e) {
            logger.error(e.getMessage());
        }
    }
}
