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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class RSSUpdater extends Thread {

    public static final int SECONDS_BETWEEN_UPDATE = 30;

    @Override
    public void run() {
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(2);
        DB.getInstance().getAllFeeds().forEach(feed -> {
            scheduledExecutorService.scheduleWithFixedDelay(new RSSFetcher(feed), 0, SECONDS_BETWEEN_UPDATE, TimeUnit.SECONDS);
        });
    }
}
