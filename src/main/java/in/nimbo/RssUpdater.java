package in.nimbo;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Slf4jReporter;
import in.nimbo.database.Database;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class RssUpdater extends Thread {
    public static final MetricRegistry metricRegistry = new MetricRegistry();
    public static final Meter fetcherMetric = metricRegistry.meter("in.nimbo.RssFetcher");

    public static final int SECONDS_BETWEEN_UPDATE = 30;

    @Override
    public void run() {
        Slf4jReporter reporter = Slf4jReporter.forRegistry(metricRegistry).build();
        reporter.start(5, TimeUnit.SECONDS);
        reporter.report();
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
        scheduledExecutorService.scheduleWithFixedDelay(() -> {
            Database.getInstance().getAllFeeds().forEach(feed -> {
                threadPoolExecutor.submit(new RssFetcher(Database.getInstance(), feed));
            });
        }, 0, SECONDS_BETWEEN_UPDATE, TimeUnit.SECONDS);
    }
}
