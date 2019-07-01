import org.apache.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Main {
    public static void main(String[] args) {
        new RSSUpdater().start();
        new ConsoleManager().start();
    }
}
