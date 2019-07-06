import database.DB;
import database.SearchQuery;
import model.Feed;
import model.Report;
import org.apache.log4j.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConsoleManager extends Thread {
    private static final String URL_REGEX = "(?<link>https?:\\/\\/(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_\\+.~#?&//=]*))";
    private Scanner scanner = new Scanner(System.in);
    private static Logger logger = Logger.getLogger(ConsoleManager.class);

    @Override
    public void run() {
        while (true) {
            String command = scanner.nextLine();
            if (command.matches("print feeds")) {
                printFeeds();
            } else if (command.matches("print reports")) {
                printReports(DB.getInstance().getAllReports());
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
            DB.getInstance().removeFeedWithReports(
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
                    String dateLowerBound = value.split(">")[0];
                    String dateUpperBound = value.split(">")[1];
                    SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
                    try {
                        searchQuery.setLowerAndUpperBound(
                                format.parse(dateLowerBound),
                                format.parse(dateUpperBound));
                    } catch (ParseException e) {
                        logger.error(e.getMessage());
                    }
                }
            }
        }
        printReports(DB.getInstance().searchReports(searchQuery));
    }

    private void addFeed(String command) {
        Matcher matcher = Pattern.compile("add " + URL_REGEX + " to feeds").matcher(command);
        if (matcher.find()) {
            DB.getInstance().insertFeed(
                    new Feed(matcher.group("link")));
        }
    }

    public void printFeeds() {
        List<Feed> feeds = DB.getInstance().getAllFeeds();
        System.out.println(":: All Feeds ::");
        feeds.forEach(feed -> {
            System.out.println(String.format("%d :: %s", feed.getId(), feed.getTitle()));
        });
    }

    public void printReports(List<Report> reports) {
        System.out.println(":: Reports ::");
        for (int i = 0; i < reports.size(); i++) {
            System.out.println(String.format("%d :: %s :: %s", (i + 1), reports.get(i).getTitle(), reports.get(i).getPubDate()));
            if (reports.get(i).getDescription() != null)
                if (reports.get(i).getDescription().length() > 100)
                    System.out.println(String.format("\t%s", reports.get(i).getDescription().substring(0, 100) + " ..."));
                else
                    System.out.println(String.format("\t%s", reports.get(i).getDescription()));
        }
    }

}
