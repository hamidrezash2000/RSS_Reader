import java.net.MalformedURLException;
import java.net.URL;

public class Main {

    public static void main(String[] args) {
        try {
            DB.getInstance().InsertFeed("FarsNews", new URL("https://www.farsnews.com/rss"));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
}
