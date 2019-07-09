package in.nimbo;

import in.nimbo.database.Database;


import java.text.SimpleDateFormat;
import java.util.Date;


public class Main {

    public static void main(String[] args) {
        new RssUpdater(Database.getInstance()).start();
        new ConsoleManager(Database.getInstance()).start();
    }
}
