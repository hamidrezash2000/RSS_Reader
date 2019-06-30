import java.net.MalformedURLException;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import java.io.IOException;
import java.net.URL;

public class Main {

    public static void main(String[] args) {
        try {
            DB.getInstance().InsertFeed("FarsNews", new URL("https://www.farsnews.com/rss"));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        String url = "https://www.mehrnews.com/rss-homepage";
        SyndFeed feed = null;
        try {
            feed = new SyndFeedInput().build(new XmlReader(new URL(url)));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (FeedException e) {
            e.printStackTrace();
        }
        System.out.println(feed.getTitle());
        for (SyndEntry entry : feed.getEntries()) {
            System.out.println(entry.getTitle());
//            entry.getPublishedDate()
//            entry.getLink()
//            entry.getDescription()
        }
    }
}
