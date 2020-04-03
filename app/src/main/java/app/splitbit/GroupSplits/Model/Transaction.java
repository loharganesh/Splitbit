package app.splitbit.GroupSplits.Model;

public class Transaction {
    private String payer,detail,key;
    private long amount,timestamp;

    public Transaction(){

    }

    public Transaction(String payer, String detail, String key, long amount, long timestamp) {
        this.payer = payer;
        this.detail = detail;
        this.key = key;
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

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
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

    @Override
    public boolean equals(@androidx.annotation.Nullable Object obj) {
        Transaction transaction = (Transaction) obj;
        return key.matches(transaction.getKey());
    }

}
