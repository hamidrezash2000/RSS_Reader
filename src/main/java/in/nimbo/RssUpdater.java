package in.nimbo;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Slf4jReporter;
import in.nimbo.database.Database;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.*;


public class RssUpdater extends Thread {
    public static final MetricRegistry metricRegistry = new MetricRegistry();
    public static final Meter fetcherMetric = metricRegistry.meter("RssFetcher");
    public static final int SECONDS_BETWEEN_UPDATE = 3;
    public static final int SECONDS_BETWEEN_FEED_CLEANING = 6;
    private HashMap<Integer, Integer> invalidLinksCache;
    private Database database;

    public RssUpdater(Database database) {
        this.database = database;
        this.invalidLinksCache = new HashMap<>();
    }

    @Override
    public void run() {
        Slf4jReporter reporter = Slf4jReporter.forRegistry(metricRegistry).build();
        reporter.start(5, TimeUnit.SECONDS);
        reporter.report();
        
        ScheduledExecutorService scheduledExecutorService =
                Executors.newSingleThreadScheduledExecutor(r -> new Thread(r, "Scheduled Rss Updater"));
        ExecutorService threadPoolExecutor = Executors.newFixedThreadPool(10);

        scheduledExecutorService.scheduleWithFixedDelay(() -> {
            Database.getInstance().getAllFeeds().forEach(feed -> {
                threadPoolExecutor.submit(new RssFetcher(database, feed, this));
            });
        }, 0, SECONDS_BETWEEN_UPDATE, TimeUnit.SECONDS);


        ScheduledExecutorService feedCleaner = Executors.newSingleThreadScheduledExecutor();
        feedCleaner.scheduleWithFixedDelay(this::handleInvalidLinks,0, SECONDS_BETWEEN_FEED_CLEANING, TimeUnit.SECONDS);
    }

    public void cacheInvalidLink(int invalidFeedId) {
        if (!invalidLinksCache.containsKey(invalidFeedId)) {
            invalidLinksCache.put(invalidFeedId, 1);
        } else {
            invalidLinksCache.put(invalidFeedId, invalidLinksCache.get(invalidFeedId) + 1);
        }
    }

    public void handleInvalidLinks() {
        for (Map.Entry<Integer, Integer> entry : invalidLinksCache.entrySet()) {
            if (2 * entry.getValue() >= SECONDS_BETWEEN_FEED_CLEANING / SECONDS_BETWEEN_UPDATE) {
                database.removeFeedWithReports(entry.getKey());
                invalidLinksCache.remove(entry.getKey());
            }
        }
        //todo is this line needed?
        invalidLinksCache.clear();
    }

}
