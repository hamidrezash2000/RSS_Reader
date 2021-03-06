package in.nimbo;

import com.codahale.metrics.Meter;
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
import net.ricecode.similarity.JaroWinklerStrategy;
import net.ricecode.similarity.SimilarityStrategy;
import net.ricecode.similarity.StringSimilarityService;
import net.ricecode.similarity.StringSimilarityServiceImpl;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class RssFetcher implements Runnable {

    private static Logger logger = Logger.getLogger(RssUpdater.class);
    private Feed feed;
    private RssUpdater rssUpdater;
    private Database database;

    public RssFetcher(Database database, Feed feed, RssUpdater rssUpdater) {
        this.database = database;
        this.feed = feed;
        this.rssUpdater = rssUpdater;
    }

    @Override
    public void run() {
        Thread.currentThread().setName(feed.getTitle());
        Meter newReportsMeter = RssUpdater.rssUpdateMetricRegistry.meter(feed.getTitle());
        try {
            SyndFeed rssFeed = new SyndFeedInput().build(
                    new XmlReader(new URL(feed.getUrl())));

            rssFeed.getEntries().parallelStream()
                    .map(this::mapToReport).filter(Objects::nonNull)
                    .filter(report -> !isDuplicate(report.getTitle()))
                    .forEach(report -> {
                        database.insertReport(report);
                        newReportsMeter.mark();
                    });
        } catch (FeedException | IOException e) {
            rssUpdater.cacheInvalidLink(feed.getId());
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

    private boolean isDuplicate(String title) {
        List<Report> lastHourReports = database.getLastHourReports();
        for (Report checkReport : lastHourReports) {
            SimilarityStrategy strategy = new JaroWinklerStrategy();
            StringSimilarityService service = new StringSimilarityServiceImpl(strategy);
            double similarity = service.score(title, checkReport.getTitle());
            if (similarity >= 0.85) {
                logger.info("Duplicate Report " + similarity + " :: " + title + " -> Similar To : " + checkReport.getTitle());
                return true;
            }
        }
        return false;
    }
}