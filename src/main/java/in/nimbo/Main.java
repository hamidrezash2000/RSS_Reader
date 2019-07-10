package in.nimbo;

import com.codahale.metrics.Slf4jReporter;
import in.nimbo.database.Database;

import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) {
        Slf4jReporter reporter = Slf4jReporter.forRegistry(RssUpdater.rssUpdateMetricRegistry).build();
        reporter.start(30, TimeUnit.SECONDS);
        reporter.report();
        Database database = Database.getInstance();
        new RssUpdater(database).start();
        new ConsoleManager(database).start();
    }
}
