import java.util.List;
import java.util.Scanner;

public class ConsoleManager extends Thread {
    private Scanner scanner = new Scanner(System.in);

    @Override
    public void run() {
        while (true) {
            String command = scanner.nextLine();
            if (command.matches("print feeds"))
                printFeeds();
            else if (command.matches("print reports"))
                printReports();
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
