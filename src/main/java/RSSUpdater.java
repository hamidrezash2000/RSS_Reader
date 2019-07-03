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
