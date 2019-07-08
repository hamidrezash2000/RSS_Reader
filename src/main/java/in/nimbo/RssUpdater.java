package in.nimbo;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Slf4jReporter;
import in.nimbo.database.Database;
import in.nimbo.model.Feed;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class RssUpdater extends Thread {
    public static final MetricRegistry metricRegistry = new MetricRegistry();
    public static final Meter fetcherMetric = metricRegistry.meter("RssFetcher");
    public static final int SECONDS_BETWEEN_UPDATE = 30;
    public static final int SECONDS_BETWEEN_FEED_CLEANING = 120;
    private HashMap<Feed, Integer> invalidLinksCache;
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

        ScheduledExecutorService updater = Executors.newScheduledThreadPool(1);
        ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
        updater.scheduleWithFixedDelay(() -> {
            Database.getInstance().getAllFeeds().forEach(feed -> {
                threadPoolExecutor.submit(new RssFetcher(database, feed, this));
            });
        }, 0, SECONDS_BETWEEN_UPDATE, TimeUnit.SECONDS);


        ScheduledExecutorService feedCleaner = Executors.newSingleThreadScheduledExecutor();
        feedCleaner.scheduleWithFixedDelay(this::handleInvalidLinks,0, SECONDS_BETWEEN_FEED_CLEANING, TimeUnit.SECONDS);
    }

    public void cacheInvalidLink(Feed invalidFeed) {
        if (invalidLinksCache.containsKey(invalidFeed)) {
            invalidLinksCache.put(invalidFeed, 1);
        } else {
            invalidLinksCache.put(invalidFeed, invalidLinksCache.get(invalidFeed) + 1);
        }
    }

    public void handleInvalidLinks() {
        for (Map.Entry<Feed, Integer> entry : invalidLinksCache.entrySet()) {
            if (2 * entry.getValue() >= SECONDS_BETWEEN_FEED_CLEANING / SECONDS_BETWEEN_UPDATE) {
                database.removeFeedWithReports(entry.getKey().getId());
                invalidLinksCache.remove(entry.getKey());
            }
        }
        //todo is this line needed?
        invalidLinksCache.clear();
    }
}
