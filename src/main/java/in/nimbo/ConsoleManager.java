package in.nimbo;

import com.github.mfathi91.time.PersianDate;
import in.nimbo.database.Database;
import in.nimbo.database.SearchQuery;
import in.nimbo.model.Feed;
import in.nimbo.model.Report;

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
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_BLUE = "\u001B[34m";
    private static final String ANSI_PURPLE = "\u001B[35m";
    private static final String ANSI_CYAN = "\u001B[36m";
    private static final String URL_REGEX = "(?<link>https?:\\/\\/(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_\\+.~#?&//=]*))";
    private static final String DATE_REGEX = "(?<year>\\d{2,4})/(?<month>\\d{1,2})/(?<day>\\d{1,2})";
    private static final String BOLD = "\033[0;1m";
    private Scanner scanner = new Scanner(System.in);
    private Database database;

    public ConsoleManager(Database database) {
        this.database = database;
    }

    @Override
    public void run() {
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
            Integer feedId = Integer.valueOf(matcher.group("feedId"));
            database.removeFeedWithReports(feedId);
            System.out.println(ANSI_RED + String.format("Feed with ID:%d removed", feedId) + ANSI_RESET);
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
            Feed feed = new Feed(matcher.group("link"));
            database.insertFeed(feed);
            System.out.println(ANSI_GREEN + String.format("Feed %s added", feed.getTitle()) + ANSI_RESET);
        }
    }

    public void printFeeds() {
        List<Feed> feeds = database.getAllFeeds();
        System.out.println(ANSI_YELLOW + BOLD + ":: All Feeds ::" + ANSI_RESET);
        feeds.forEach(feed -> {
            System.out.println(ANSI_BLUE + String.format("%d :: %s", feed.getId(), feed.getTitle()) + ANSI_RESET);
        });
    }

    public void printReports(List<Report> reports) {
        System.out.println(ANSI_YELLOW  + BOLD + ":: Reports ::" + ANSI_RESET);
        for (int i = 0; i < reports.size(); i++) {
            PersianDate persianDate = PersianDate.fromGregorian(
                    reports.get(i).getPubDate().toInstant()
                            .atZone(ZoneId.systemDefault()).toLocalDate());

            String time = getTimeOfDate(reports.get(i).getPubDate());

            System.out.println(ANSI_PURPLE  + BOLD + String.format("%d :: %s :: %s %s", (i + 1), reports.get(i).getTitle(), persianDate, time) + ANSI_RESET);
            if (reports.get(i).getDescription() != null)
                if (reports.get(i).getDescription().length() > 100)
                    System.out.println(ANSI_CYAN + String.format("\t%s", reports.get(i).getDescription().substring(0, 100) + " ...") + ANSI_RESET);
                else
                    System.out.println(ANSI_CYAN + String.format("\t%s", reports.get(i).getDescription()) + ANSI_RESET);
        }
    }

    private String getTimeOfDate(Date date) {
        StringBuffer timeBuffer = new StringBuffer();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        simpleDateFormat.format(date, timeBuffer, new FieldPosition(0));
        return timeBuffer.toString();
    }
}
