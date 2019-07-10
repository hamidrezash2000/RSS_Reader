package in.nimbo;

import in.nimbo.database.Database;

public class Main {

    public static void main(String[] args) {
        Database database = Database.getInstance();
        new RssUpdater(database).start();
        new ConsoleManager(database).start();
    }
}
