import java.util.Date;

public class Report {
    private int feedId;
    private String title;
    private String link;
    private Date pubDate;
    private String description;

    public Report(int feedId, String title, String link) {
        this.feedId = feedId;
        this.title = title;
        this.link = link;
    }

    public Report(int feedId, String title, String link, Date pubDate, String description) {
        this.feedId = feedId;
        this.title = title;
        this.link = link;
        this.pubDate = pubDate;
        this.description = description;
    }

    public int getFeedId() {
        return feedId;
    }

    public void setFeedId(int feedId) {
        this.feedId = feedId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Date getPubDate() {
        return pubDate;
    }

    public void setPubDate(Date pubDate) {
        this.pubDate = pubDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Report &&
                ((Report) obj).getTitle().equals(title) &&
                ((Report) obj).getLink().equals(link);
    }
}
