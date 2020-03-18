package app.splitbit.GroupSplits.Model;

public class Event {
    private String key,eventname,eventpicture,eventadmin;

    public Event(){

    }

    public Event(String key, String eventname, String eventpicture, String eventadmin) {
        this.key = key;
        this.eventname = eventname;
        this.eventpicture = eventpicture;
        this.eventadmin = eventadmin;
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
}
