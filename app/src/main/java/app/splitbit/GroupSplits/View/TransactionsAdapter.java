package app.splitbit.GroupSplits.View;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.FirebaseFunctionsException;
import com.google.firebase.functions.HttpsCallableResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import app.splitbit.Application.Timestamp;
import app.splitbit.Authentication.Signin;
import app.splitbit.GroupSplits.Model.Transaction;
import app.splitbit.R;
import app.splitbit.Settings.Settings;

public class TransactionsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private ArrayList<Transaction> arraylist_transactions;
    private Context context;
    private String event;

    public TransactionsAdapter(ArrayList<Transaction> arraylist_transactions, Context context,String event) {
        this.arraylist_transactions = arraylist_transactions;
        this.context = context;
        this.event = event;
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

        String time = Timestamp.getDate(transaction.getTimestamp()) +" at "+ Timestamp.getTime(transaction.getTimestamp())+" • ";

        //-- Getting User Info
        FirebaseDatabase.getInstance().getReference().child("splitbit/users/"+transaction.getPayer()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String username = dataSnapshot.child("name").getValue().toString();
                    if(transaction.getPayer().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                        ((TransactionViewHolder)holder).textView_transaction_title.setText(Html.fromHtml("<b>You've</b>"+" Paid <font color='#ff4180'>"+transaction.getAmount()+" Rs."));
                    }else{
                        ((TransactionViewHolder)holder).textView_transaction_title.setText(Html.fromHtml("<b>"+username+"</b> Paid <font color='#ff4180'>"+transaction.getAmount()+" Rs."));
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        ((TransactionViewHolder)holder).textView_transaction_body.setText(time+"for "+transaction.getDetail());

        ((TransactionViewHolder)holder).itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(FirebaseAuth.getInstance().getCurrentUser().getUid().equals(transaction.getPayer())){
                    menuDialong(transaction);
                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return arraylist_transactions.size();
    }



    //--
    public void menuDialong(final Transaction transaction){
        // Create custom dialog object
        final Dialog dialog = new Dialog(context,R.style.DialogCustomTheme);
        // Include dialog.xml file
        dialog.setContentView(R.layout.dialog_transaction_menu);
        // Set dialog title
        dialog.setTitle("");

        TextView delete = (TextView) dialog.findViewById(R.id.dialog_delete_transaction_textview);

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                deleteTransactionDialong(transaction);
            }
        });

        dialog.show();

    }

    public void deleteTransactionDialong(final Transaction transaction){
        // Create custom dialog object
        final Dialog dialog = new Dialog(context,R.style.DialogCustomTheme);
        // Include dialog.xml file
        dialog.setContentView(R.layout.dialog_delete_transaction);
        // Set dialog title
        dialog.setTitle("");


        final Button delete = dialog.findViewById(R.id.dialog_delete_button);
        final TextView cancel = dialog.findViewById(R.id.dialog_cancel_button);

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete.setText("Deleting...");
                dialog.setCanceledOnTouchOutside(false);
                dialog.setCancelable(false);
                //-- Transaction Deletion
                removeTransaction(transaction.getKey(),""+transaction.getAmount(),event)
                        .addOnCompleteListener(new OnCompleteListener<String>() {
                            @Override
                            public void onComplete(@NonNull Task<String> task) {
                                if (!task.isSuccessful()) {
                                    Exception e = task.getException();
                                    if (e instanceof FirebaseFunctionsException) {
                                        FirebaseFunctionsException ffe = (FirebaseFunctionsException) e;
                                        FirebaseFunctionsException.Code code = ffe.getCode();
                                        Object details = ffe.getDetails();
                                    }

                                    // ...
                                    Toast.makeText(context, "Oops! Something went wrong!", Toast.LENGTH_SHORT).show();
                                    delete.setText("Retry");
                                    cancel.setText("Go back");
                                }else{
                                    dialog.cancel();
                                }
                            }
                        });

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });



        dialog.show();

    }

    //-- Delete Transaction Task
    private Task<String> removeTransaction(String transaction,String amount,String event) {

        // Create the arguments to the callable function.
        Map<String,Object> data = new HashMap<>();
        data.put("event",event);
        data.put("amount",amount);
        data.put("transaction",transaction);



        return FirebaseFunctions.getInstance()
            .getHttpsCallable("removeTransaction")
            .call(data)
            .continueWith(new Continuation<HttpsCallableResult, String>() {
                @Override
                public String then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                    // This continuation runs on either success or failure, but if the task
                    // has failed then getResult() will throw an Exception which will be
                    // propagated down.
                    String result = (String) task.getResult().getData();
                    return result;
                }
            });
    }

}
