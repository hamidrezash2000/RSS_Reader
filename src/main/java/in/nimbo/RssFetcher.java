package in.nimbo;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import de.l3s.boilerpipe.BoilerpipeProcessingException;
import de.l3s.boilerpipe.extractors.ArticleExtractor;
import in.nimbo.database.Database;
import in.nimbo.model.Feed;
import in.nimbo.model.Report;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.Objects;

public class RssFetcher implements Runnable {

    private static Logger logger = Logger.getLogger(RssUpdater.class);
    private Feed feed;
    private Database database;

    public RssFetcher(Database database, Feed feed) {
        this.database = database;
        this.feed = feed;
    }

    @Override
    public void run() {
        Thread.currentThread().setName(getThreadName());
        RssUpdater.fetcherMetric.mark();
        try {
            SyndFeed rssFeed = new SyndFeedInput().build(
                    new XmlReader(new URL(feed.getUrl())));

            rssFeed.getEntries().parallelStream()
                    .map(this::mapToReport).filter(Objects::nonNull)
                    .filter(report -> database.reportNotExists(report.getLink()))
                    .forEach(report -> database.insertReport(report));
        } catch (FeedException | IOException e) {
            logger.error(e.getMessage());
        }
    }

    private Report mapToReport(SyndEntry entry) {
        Report newReport = new Report(feed.getId(), entry.getTitle(), entry.getLink());
        setPubDate(entry.getPublishedDate(), newReport);
        extractMainContentOfReport(entry.getLink(), newReport);
        return newReport;
    }

    private void setPubDate(Date pubDate, Report newReport) {
        try {
            newReport.setPubDate(pubDate);
        } catch (NullPointerException e) {
            logger.info("Couldn't find pubDate of report");
        }
    }

    private void extractMainContentOfReport(String url, Report newReport) {
        try {
            String fullTextOfReport = ArticleExtractor.INSTANCE.getText(new URL(url));
            newReport.setDescription(fullTextOfReport);
        } catch (NullPointerException e) {
            logger.info("Couldn't find full text of report");
        } catch (MalformedURLException | BoilerpipeProcessingException e) {
            logger.error(e.getMessage());
        }
    }

    public String getThreadName() {
        return feed.getTitle() + "Fetcher";
    }
}