package in.nimbo;

import in.nimbo.database.Database;


import java.text.SimpleDateFormat;
import java.util.Date;


public class Main {

    static {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy-hh-mm-ss");
        System.setProperty("current.date.time", dateFormat.format(new Date()));
    }

    public static void main(String[] args) {
        new RssUpdater(Database.getInstance()).start();
        new ConsoleManager().start();
    }
}
