package app.splitbit.GroupSplits.Model;

public class User {
    private String name,key,picture,username;

    public User(){

    }

    public User(String name, String key, String picture, String username) {
        this.name = name;
        this.key = key;
        this.picture = picture;
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
