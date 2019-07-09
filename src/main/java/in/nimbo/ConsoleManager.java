package in.nimbo;

import com.github.mfathi91.time.PersianDate;
import in.nimbo.database.Database;
import in.nimbo.database.SearchQuery;
import in.nimbo.model.Feed;
import in.nimbo.model.Report;
import org.apache.log4j.Logger;

import java.text.FieldPosition;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConsoleManager extends Thread {
    private static final String URL_REGEX = "(?<link>https?:\\/\\/(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_\\+.~#?&//=]*))";
    private static final String DATE_REGEX = "(?<year>\\d{2,4})/(?<month>\\d{1,2})/(?<day>\\d{1,2})";
    private static Logger logger = Logger.getLogger(ConsoleManager.class);
    private Scanner scanner = new Scanner(System.in);
    private Database database;

    public ConsoleManager(Database database) {
        this.database = database;
    }

    @Override
    public void run() {
        // TODO: 7/10/19 handle active and not active feeds
        while (true) {
            String command = scanner.nextLine();
            if (command.matches("print feeds")) {
                printFeeds();
            } else if (command.matches("print reports")) {
                printReports(database.getAllReports());
            } else if (command.matches("add " + URL_REGEX + " to feeds")) {
                addFeed(command);
            } else if (command.matches("remove \\d+ from feeds")) {
                removeFeed(command);
            } else if (command.matches("search .*")) {
                searchHandler(command);
            }
        }
    }

    private void removeFeed(String command) {
        Matcher matcher = Pattern.compile("remove (?<feedId>\\d+) from feeds").matcher(command);
        if (matcher.find()) {
            database.removeFeedWithReports(
                    Integer.valueOf(matcher.group("feedId")));
        }
    }

    private void searchHandler(String command) {
        SearchQuery searchQuery = new SearchQuery();
        command = command.replace("search ", "");
        String[] parameters = command.split("\\s*-\\s*");
        for (String parameter : parameters) {
            if (parameter.matches("\\S+:\\S+")) {
                String key = parameter.split(":")[0];
                String value = parameter.split(":")[1];
                if (key.equalsIgnoreCase("title")) {
                    searchQuery.setTitle(value);
                } else if (key.equalsIgnoreCase("description")) {
                    searchQuery.setDescription(value);
                } else if (key.equalsIgnoreCase("feedId")) {
                    searchQuery.setFeedId(Integer.valueOf(value));
                } else if (key.equalsIgnoreCase("pubDate")) {
                    String dateLowerBoundString = value.split(">")[0];
                    String dateUpperBoundString = value.split(">")[1];
                    Matcher dateLowerBoundMatcher = Pattern.compile(DATE_REGEX).matcher(dateLowerBoundString);
                    Matcher dateUpperBoundMatcher = Pattern.compile(DATE_REGEX).matcher(dateUpperBoundString);
                    if (dateLowerBoundMatcher.find() && dateUpperBoundMatcher.find()){
                        Date dateLowerBound = Date.from(Instant.from(PersianDate.of(
                                Integer.valueOf(dateLowerBoundMatcher.group("year")),
                                Integer.valueOf(dateLowerBoundMatcher.group("month")),
                                Integer.valueOf(dateLowerBoundMatcher.group("day"))).toGregorian()
                                .atStartOfDay(ZoneId.systemDefault()).toInstant()));

                        Date dateUpperBound = Date.from(Instant.from(PersianDate.of(
                                Integer.valueOf(dateUpperBoundMatcher.group("year")),
                                Integer.valueOf(dateUpperBoundMatcher.group("month")),
                                Integer.valueOf(dateUpperBoundMatcher.group("day"))).toGregorian()
                                .atStartOfDay(ZoneId.systemDefault()).toInstant()));

                        searchQuery.setLowerAndUpperBound(dateLowerBound, dateUpperBound);
                    }
                }
            }
        }
        printReports(database.searchReports(searchQuery));
    }

    private void addFeed(String command) {
        Matcher matcher = Pattern.compile("add " + URL_REGEX + " to feeds").matcher(command);
        if (matcher.find()) {
            database.insertFeed(
                    new Feed(matcher.group("link")));
        }
    }

    public void printFeeds() {
        List<Feed> feeds = database.getAllFeeds();
        System.out.println(":: All Feeds ::");
        feeds.forEach(feed -> {
            System.out.println(String.format("%d :: %s", feed.getId(), feed.getTitle()));
        });
    }

    public void printReports(List<Report> reports) {
        System.out.println(":: Reports ::");
        for (int i = 0; i < reports.size(); i++) {
            PersianDate persianDate = PersianDate.fromGregorian(
                    reports.get(i).getPubDate().toInstant()
                            .atZone(ZoneId.systemDefault()).toLocalDate());

            String time = getTimeOfDate(reports.get(i).getPubDate());

            System.out.println(String.format("%d :: %s :: %s %s", (i + 1), reports.get(i).getTitle(), persianDate, time));
            if (reports.get(i).getDescription() != null)
                if (reports.get(i).getDescription().length() > 100)
                    System.out.println(String.format("\t%s", reports.get(i).getDescription().substring(0, 100) + " ..."));
                else
                    System.out.println(String.format("\t%s", reports.get(i).getDescription()));
        }
    }

    private String getTimeOfDate(Date date) {
        StringBuffer timeBuffer = new StringBuffer();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        simpleDateFormat.format(date, timeBuffer, new FieldPosition(0));
        return timeBuffer.toString();
    }

}
