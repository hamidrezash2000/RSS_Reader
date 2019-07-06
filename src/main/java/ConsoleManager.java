import database.DB;
import model.Feed;
import model.Report;

import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConsoleManager extends Thread {
    private Scanner scanner = new Scanner(System.in);

    @Override
    public void run() {
        while (true) {
            String command = scanner.nextLine();
            if (command.matches("print feeds")) {
                printFeeds();
            } else if (command.matches("print reports")) {
                printReports();
            } else if (command.matches("add https?:\\/\\/(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_\\+.~#?&//=]*) to feeds")) {
                addFeed(command);
            }
        }
    }

    private void addFeed(String command) {
        Matcher matcher = Pattern.compile("add (?<link>https?:\\/\\/(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_\\+.~#?&//=]*)) to feeds").matcher(command);
        if (matcher.find()) {
            DB.getInstance().insertFeed(
                    new Feed(matcher.group("link")));
        }
    }

    public void printFeeds() {
        List<Feed> feeds = DB.getInstance().getAllFeeds();
        for (int i = 0; i < feeds.size(); i++) {
            System.out.println(String.format("%d :: %s", (i + 1), feeds.get(i).getTitle()));
        }
    }

    public void printReports() {
        List<Report> reports = DB.getInstance().getAllReports();
        for (int i = 0; i < reports.size(); i++) {
            System.out.println(String.format("%d :: %s", (i + 1), reports.get(i).getTitle()));
            System.out.println(String.format("\t%s", reports.get(i).getDescription()));
        }
    }
}
