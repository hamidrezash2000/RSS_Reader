package database;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

public class SearchQuery {
    Optional<String> title = Optional.empty();
    Optional<String> description = Optional.empty();
    Optional<Date> lowerBound = Optional.empty(), upperBound = Optional.empty();
    Optional<Integer> feedId = Optional.empty();

    public String generateQuery() {
        // todo for an empty condition this query is not valid
        StringBuilder res = new StringBuilder("SELECT feedId, title, link FROM reports WHERE ");
        boolean anyConditionAdded = false;
        if (title.isPresent()) {
            res.append(anyConditionAdded ? "AND " : "");
            res.append(String.format("title LIKE '%%%s%%' ", title.get()));
            anyConditionAdded = true;
        }
        if (description.isPresent()) {
            res.append(anyConditionAdded ? "AND " : "");
            res.append(String.format("description LIKE '%%%s%%' ", description.get()));
            anyConditionAdded = true;
        }
        if (lowerBound.isPresent() && upperBound.isPresent()) {
            res.append(anyConditionAdded ? "AND " : "");
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            res.append(String.format("pubDate BETWEEN '%s' AND '%s' ", dateFormat.format(lowerBound.get()), dateFormat.format(upperBound.get())));
        }
        if (feedId.isPresent()) {
            res.append(anyConditionAdded ? "AND " : "");
            res.append(String.format("feedId = %d ", feedId.get()));
        }
        return res.toString();
    }

    public String getTitle() {
        return title.get();
    }

    public void setTitle(String title) {
        this.title = Optional.ofNullable(title);
    }

    public String getDescription() {
        return description.get();
    }

    public void setDescription(String description) {
        this.description = Optional.ofNullable(description);
    }

    public Date getLowerBound() {
        return lowerBound.get();
    }

    public void setLowerAndUpperBound(Date lowerBound, Date upperBound) {
        this.lowerBound = Optional.ofNullable(lowerBound);
        this.upperBound = Optional.ofNullable(upperBound);
    }

    public Date getUpperBound() {
        return upperBound.get();
    }

    public int getFeedId() {
        return feedId.get();
    }

    public void setFeedId(int feedId) {
        this.feedId = Optional.ofNullable(feedId);
    }
}
