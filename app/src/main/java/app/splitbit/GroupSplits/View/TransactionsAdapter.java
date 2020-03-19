package app.splitbit.GroupSplits.View;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import app.splitbit.GroupSplits.EventRoom;
import app.splitbit.GroupSplits.Model.Event;
import app.splitbit.GroupSplits.Model.Transaction;
import app.splitbit.R;

public class TransactionsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private ArrayList<Transaction> arraylist_transactions;
    private Context context;

    public TransactionsAdapter(ArrayList<Transaction> arraylist_transactions, Context context) {
        this.arraylist_transactions = arraylist_transactions;
        this.context = context;
    }

    public static class TransactionViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        private TextView textView_transaction_title;
        private TextView textView_transaction_body;
        private TextView textView_transaction_amount;
        public TransactionViewHolder(View v) {
            super(v);
            textView_transaction_title = (TextView) v.findViewById(R.id.textview_transaction_title);
            textView_transaction_body = (TextView) v.findViewById(R.id.textview_transaction_body);
            textView_transaction_amount = (TextView) v.findViewById(R.id.textview_transaction_amount);
        }
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_transaction, parent, false);
        return new TransactionViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Transaction transaction = arraylist_transactions.get(position);

        //-- Getting User Info
        ((TransactionViewHolder)holder).textView_transaction_title.setText(transaction.getPayer()+" Paid");
        ((TransactionViewHolder)holder).textView_transaction_body.setText("For - "+transaction.getDetail());
        ((TransactionViewHolder)holder).textView_transaction_amount.setText(transaction.getAmount()+" Rs.");

    }

    @Override
    public int getItemCount() {
        return arraylist_transactions.size();
    }
}
