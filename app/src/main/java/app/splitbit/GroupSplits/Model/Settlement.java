package app.splitbit.GroupSplits.Model;

public class Settlement {

    private String payer,reciever;
    private String amount;

    public Settlement(){

    }

    public Settlement(String payer, String reciever, String amount) {
        this.payer = payer;
        this.reciever = reciever;
        this.amount = amount;
    }

    public String getPayer() {
        return payer;
    }

    public void setPayer(String payer) {
        this.payer = payer;
    }

    public String getReciever() {
        return reciever;
    }

    public void setReciever(String reciever) {
        this.reciever = reciever;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
