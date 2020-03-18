package app.splitbit.GroupSplits;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.FirebaseFunctionsException;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;

import app.splitbit.GroupSplits.Model.User;
import app.splitbit.GroupSplits.View.UserAdapter;
import app.splitbit.R;

public class CreateEvent extends AppCompatActivity implements AdapterView.OnItemClickListener{

    private EditText input_eventname;
    private Button button_addmember;
    private Button button_createevent;
    private ListView members_listview;
    private ProgressBar progressBar;

    //Lists
    private ArrayList<User> members  = new ArrayList<>();
    private ArrayList<User> friends = new ArrayList<>();

    //ArrayAdapter
    private UserAdapter friendsAdapter;
    private UserAdapter membersAdapter;

    //Firebase
    private DatabaseReference db;
    private DatabaseReference splitgroupsDB;
    private FirebaseAuth auth;
    private FirebaseFunctions functions;


    private void initActivityUI(){
        input_eventname = (EditText) findViewById(R.id.input_eventname);
        button_addmember = (Button) findViewById(R.id.button_addmember);
        button_createevent = (Button) findViewById(R.id.button_createevent);
        members_listview = (ListView) findViewById(R.id.listview_members);

        progressBar = (ProgressBar) findViewById(R.id.progressBar_creatingevent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        getSupportActionBar().setTitle("Create Split Event");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initActivityUI();

        //------ DEMO FRIENDS LIST
        friends.add(new User("Sandeep Lohar","MbJQ4mg85ehzd6BhNdaEqkjvilg1","pic"));
        friends.add(new User("About !","bauy1G1NbRX5WXsL7hNCejozUkB2","pic"));
        friends.add(new User("Computer Tricks","fF5Pp2hYnKfkVquHOUhdZBptUYh1","pic"));
        //---

        friendsAdapter = new UserAdapter (CreateEvent.this, 0, friends);
        membersAdapter = new UserAdapter (CreateEvent.this, 0, members);

        members_listview.setAdapter(membersAdapter);


        db = FirebaseDatabase.getInstance().getReference().child("splitbit");
        splitgroupsDB = db.child("groupevents");
        auth = FirebaseAuth.getInstance();
        functions = FirebaseFunctions.getInstance();


        button_addmember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMemberChooser();
            }
        });

        button_createevent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(input_eventname.getText().toString()) && members.size() >= 3){
                    progressBar.setVisibility(View.VISIBLE);
                    manageCreateEventUIControls(false);

                    //Calling Function
                    updateDB(input_eventname.getText().toString())
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
                                    Toast.makeText(CreateEvent.this, "Oops! Something went wrong", Toast.LENGTH_SHORT).show();
                                    e.printStackTrace();
                                }else{
                                    progressBar.setVisibility(View.GONE);
                                    manageCreateEventUIControls(true);
                                    startActivity(new Intent(CreateEvent.this,EventRoom.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY));
                                    finish();
                                }

                                // ...
                            }
                        });
                }

                if(TextUtils.isEmpty(input_eventname.getText().toString())){
                    input_eventname.setError("Enter Event Name");
                }else if(members.size() < 3){
                    Toast.makeText(CreateEvent.this, "Select atleast 2 members", Toast.LENGTH_SHORT).show();
                }else{

                }

            }
        });


    }

    private void showMemberChooser(){
        members.clear();
        ListView lv = new ListView(CreateEvent.this);
        lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        lv.setOnItemClickListener(this);
        lv.setAdapter(friendsAdapter);

        AlertDialog.Builder bldr = new AlertDialog.Builder(this);
        bldr.setCancelable(false);
        bldr.setTitle("");
        bldr.setView(lv);
        bldr.setPositiveButton("Done",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        members.add(new User("Ganesh Lohar",auth.getCurrentUser().getUid(),"pic"));
                        membersAdapter.notifyDataSetChanged();
                    }
                });
        bldr.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        members.clear();
                        membersAdapter.notifyDataSetChanged();
                    }
                });

        final Dialog dlg = bldr.create();
        dlg.show();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        User member = (User) parent.getItemAtPosition(position);
        members.add(member);
    }


    /*public void updateDB(){
        progressBar.setVisibility(View.VISIBLE);
        manageCreateEventUIControls(false);

        final String key = splitgroupsDB.push().getKey();
        HashMap<String,Object> event = new HashMap<>();
        event.put("name",input_eventname.getText().toString());
        event.put("key",key);
        event.put("picture","pictureURL");
        event.put("admin",auth.getCurrentUser().getUid());
        event.put("timestamp", ServerValue.TIMESTAMP);

        splitgroupsDB.child(key).setValue(event)
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                   for(int i=0;i<members.size();i++){
                       User member = members.get(i);
                       if(member.getKey().equals(auth.getCurrentUser().getUid()))
                           splitgroupsDB.child(key).child("members").child(member.getKey()).setValue("admin");
                       else
                           splitgroupsDB.child(key).child("members").child(member.getKey()).setValue("member");
                   }
                    progressBar.setVisibility(View.GONE);
                    manageCreateEventUIControls(true);
                }
            })

            .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(CreateEvent.this, "Event Creation Failed! Try again", Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
                manageCreateEventUIControls(true);
            }
        });
    }*/

    private Task<String> updateDB(String name) {
        // Create the arguments to the callable function.
        String members_json = new Gson().toJson(members);
        HashMap<String,Object> data = new HashMap<>();
        data.put("members",members_json);
        data.put("eventname",name);
        data.put("timestamp",ServerValue.TIMESTAMP);
        data.put("picture","picture");

        return functions
                .getHttpsCallable("createSplitEvent")
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

    public void manageCreateEventUIControls(boolean activate){
        button_createevent.setEnabled(activate);
        button_addmember.setEnabled(activate);
        input_eventname.setEnabled(activate);
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
