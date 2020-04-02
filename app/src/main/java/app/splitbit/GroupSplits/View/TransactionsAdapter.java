package app.splitbit.GroupSplits.View;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
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

        public TransactionViewHolder(View v) {
            super(v);
            textView_transaction_title = (TextView) v.findViewById(R.id.textview_transaction_title);
            textView_transaction_body = (TextView) v.findViewById(R.id.textview_transaction_body);
        }
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_transaction, parent, false);
        return new TransactionViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        final Transaction transaction = arraylist_transactions.get(position);

        //-- Getting User Info
        FirebaseDatabase.getInstance().getReference().child("splitbit/users/"+transaction.getPayer()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String username = dataSnapshot.child("name").getValue().toString();
                    ((TransactionViewHolder)holder).textView_transaction_title.setText(Html.fromHtml(username+" Paid <font color='#ff4180'>"+transaction.getAmount()+" Rs."));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        ((TransactionViewHolder)holder).textView_transaction_body.setText("For - "+transaction.getDetail());

    }

    @Override
    public int getItemCount() {
        return arraylist_transactions.size();
    }

}
