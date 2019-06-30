import java.net.MalformedURLException;
import java.net.URL;

public class Main {

    public static void main(String[] args) {
        try {
            DB.getInstance().InsertFeed("MehrNews", new URL("https://www.mehrnews.com/rss"));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
}
