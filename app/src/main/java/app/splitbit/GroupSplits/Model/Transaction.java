package app.splitbit.GroupSplits.Model;

public class Transaction {
    private String payer,detail,entryby;
    private long amount,timestamp;

    public Transaction(){

    }

    public Transaction(String payer, String detail, String entryby, long amount, long timestamp) {
        this.payer = payer;
        this.detail = detail;
        this.entryby = entryby;
        this.amount = amount;
        this.timestamp = timestamp;
    }

    public String getPayer() {
        return payer;
    }

    public void setPayer(String payer) {
        this.payer = payer;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getEntryby() {
        return entryby;
    }

    public void setEntryby(String entryby) {
        this.entryby = entryby;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
