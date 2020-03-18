package app.splitbit.GroupSplits;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.FirebaseFunctionsException;
import com.google.firebase.functions.HttpsCallableResult;

import java.util.ArrayList;
import java.util.HashMap;

import app.splitbit.GroupSplits.Model.User;
import app.splitbit.GroupSplits.View.UserAdapter;
import app.splitbit.R;

public class AddTransaction extends AppCompatActivity implements AdapterView.OnItemClickListener {

    //Firebase
    private DatabaseReference db;
    private FirebaseAuth auth;
    private FirebaseFunctions functions;

    //UI
    private EditText edit_amount;
    private EditText edit_description;
    private Button button_selectpayer;
    private Button button_addtransaction;
    private ProgressBar progressBar;

    private Dialog dialog;


    //ArrayLists
    private ArrayList<User> friends = new ArrayList<>();

    //ArrayAdapter
    private UserAdapter friendsAdapter;

    //Strings
    private String PAYER_ID;
    private String EVENT_ROOM_KEY;

    private void initActivityUI(){
        getSupportActionBar().setTitle("Add Transaction");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        edit_amount = (EditText) findViewById(R.id.edit_amount);
        edit_description = (EditText) findViewById(R.id.edit_description);
        progressBar = (ProgressBar) findViewById(R.id.progressbar_adding_transaction);
        button_selectpayer = (Button) findViewById(R.id.button_sellectpayer);
        button_addtransaction = (Button) findViewById(R.id.button_add_transaction);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);

        EVENT_ROOM_KEY = getIntent().getStringExtra("key");

        //Initiaizing Activity UI
        initActivityUI();

        functions = FirebaseFunctions.getInstance();

        friends = new ArrayList<>();

        //------ DEMO FRIENDS LIST
        friends.add(new User("Sandeep Lohar","MbJQ4mg85ehzd6BhNdaEqkjvilg1","pic"));
        friends.add(new User("About !","bauy1G1NbRX5WXsL7hNCejozUkB2","pic"));
        friends.add(new User("Computer Tricks","fF5Pp2hYnKfkVquHOUhdZBptUYh1","pic"));
        //---

        friendsAdapter = new UserAdapter (AddTransaction.this, 0, friends);

        button_selectpayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPayerChooser();
            }
        });

        button_addtransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( !TextUtils.isEmpty(edit_amount.getText().toString()) && !TextUtils.isEmpty(edit_amount.getText().toString()) && !PAYER_ID.equals("")){
                    progressBar.setVisibility(View.VISIBLE);
                    //--
                    //Calling Function
                    addTransaction(edit_amount.getText().toString(),edit_description.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<String>() {
                                @Override
                                public void onComplete(@NonNull Task<String> task) {
                                    if (!task.isSuccessful()) {
                                        Exception e = task.getException();
                                        e.printStackTrace();
                                        if (e instanceof FirebaseFunctionsException) {
                                            FirebaseFunctionsException ffe = (FirebaseFunctionsException) e;
                                            FirebaseFunctionsException.Code code = ffe.getCode();
                                            Object details = ffe.getDetails();
                                        }
                                        // ...
                                        Toast.makeText(AddTransaction.this, "Opps! Something went wrong", Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.GONE);
                                    }else{
                                        progressBar.setVisibility(View.GONE);
                                        onBackPressed();
                                    }

                                    // ...
                                }
                            });

                }else{
                    Toast.makeText(AddTransaction.this, "All Inputs are mandatory", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showPayerChooser(){
        PAYER_ID = "";
        ListView lv = new ListView(AddTransaction.this);
        lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        lv.setOnItemClickListener(this);
        lv.setAdapter(friendsAdapter);

        AlertDialog.Builder bldr = new AlertDialog.Builder(this);
        bldr.setCancelable(false);
        bldr.setTitle("");
        bldr.setView(lv);

        bldr.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PAYER_ID = "";
                        button_selectpayer.setText("Select the payer");
                    }
                });

        dialog = bldr.create();
        dialog.show();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        User user = friends.get(position);
        PAYER_ID = user.getKey();
        button_selectpayer.setText(user.getName()+" is Paying (Tap to change)");
        dialog.cancel();
    }

    private Task<String> addTransaction(String amt,String desc){

        HashMap<String,Object> data = new HashMap<>();
        data.put("amount",amt);
        data.put("description",desc);
        data.put("payerid",PAYER_ID);
        data.put("eventkey",EVENT_ROOM_KEY);

        return functions
                .getHttpsCallable("addTransaction")
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
