import database.DB;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class RSSUpdater extends Thread {

    public static final int SECONDS_BETWEEN_UPDATE = 30;

    @Override
    public void run() {
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
        scheduledExecutorService.scheduleWithFixedDelay(() -> {
            DB.getInstance().getAllFeeds().forEach(feed -> {
                threadPoolExecutor.submit(new RSSFetcher(feed));
            });
        }, 0, SECONDS_BETWEEN_UPDATE, TimeUnit.SECONDS);
    }
}
