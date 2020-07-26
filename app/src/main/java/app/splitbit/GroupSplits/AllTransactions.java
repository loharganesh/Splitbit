package app.splitbit.GroupSplits;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import app.splitbit.GroupSplits.Model.Transaction;
import app.splitbit.GroupSplits.View.TransactionsAdapter;
import app.splitbit.R;

public class AllTransactions extends AppCompatActivity {

    //-- Strings
    private String EVENT_ROOM_ID;

    //-- UI
    private RecyclerView recyclerView_transactions;
    private ArrayList<Transaction> arraylist_transactions;
    private TransactionsAdapter transactionsAdapter;

    //-- Firebase
    private DatabaseReference db;
    private FirebaseAuth auth;

    private void initActivityUI(){
        getSupportActionBar().setTitle("Transactions");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView_transactions = (RecyclerView) findViewById(R.id.recyclerview_transactions);
        arraylist_transactions = new ArrayList<>();
        transactionsAdapter = new TransactionsAdapter(arraylist_transactions,AllTransactions.this,EVENT_ROOM_ID);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);

        recyclerView_transactions.setLayoutManager(layoutManager);
        recyclerView_transactions.setAdapter(transactionsAdapter);


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_transactions);

        //-- Initializing Activity UI
        initActivityUI();

        //-- Strings
        EVENT_ROOM_ID = getIntent().getStringExtra("key");

        //--  Firebase
        db = FirebaseDatabase.getInstance().getReference().child("splitbit");
        auth = FirebaseAuth.getInstance();

        //--
        loadTransactions();

    }

    private void loadTransactions(){
        db.child("transactions").child(EVENT_ROOM_ID).orderByChild("timestamp").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){
                    for(DataSnapshot transactionSnap:dataSnapshot.getChildren()){
                        Transaction transaction = transactionSnap.getValue(Transaction.class);
                        arraylist_transactions.add(transaction);
                    }
                    transactionsAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {

            case android.R.id.home:
                super.onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
