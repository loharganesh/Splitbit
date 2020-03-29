package app.splitbit.GroupSplits.Model;

public class Member {
    private String key,name;
    private long amount;

    public Member(){

    }

    public Member(String key, String name, long amount) {
        this.key = key;
        this.name = name;
        this.amount = amount;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }
}
