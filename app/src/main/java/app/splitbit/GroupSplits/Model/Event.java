package app.splitbit.GroupSplits.Model;

public class Event {
    private String key,eventname,eventpicture,eventadmin;
    private long timestamp;

    public Event(){

    }

    public Event(String key, String eventname, String eventpicture, String eventadmin, long timestamp) {
        this.key = key;
        this.eventname = eventname;
        this.eventpicture = eventpicture;
        this.eventadmin = eventadmin;
        this.timestamp = timestamp;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getEventname() {
        return eventname;
    }

    public void setEventname(String eventname) {
        this.eventname = eventname;
    }

    public String getEventpicture() {
        return eventpicture;
    }

    public void setEventpicture(String eventpicture) {
        this.eventpicture = eventpicture;
    }

    public String getEventadmin() {
        return eventadmin;
    }

    public void setEventadmin(String eventadmin) {
        this.eventadmin = eventadmin;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(@androidx.annotation.Nullable Object obj) {
        Event event = (Event) obj;
        return key.matches(event.getKey());
    }

}
