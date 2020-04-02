package app.splitbit.GroupSplits;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.text.TextUtilsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.FirebaseFunctionsException;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;

import app.splitbit.GroupSplits.Model.User;
import app.splitbit.GroupSplits.View.SelectedMembersAdapter;
import app.splitbit.GroupSplits.View.UserAdapter;
import app.splitbit.R;

public class CreateEvent extends AppCompatActivity{

    private EditText input_eventname;
    private EditText input_username_query;
    private TextView button_addmember;
    private Button button_createevent;
    private RecyclerView recyclerView_members;
    private RecyclerView recyclerView_user;
    private ProgressBar progressBar;

    //Lists
    private ArrayList<User> members  = new ArrayList<>();
    private HashMap<Object,String> member_meta = new HashMap<>();

    //ArrayAdapter
    private SelectedMembersAdapter membersAdapter;

    //Firebase
    private DatabaseReference db;
    private DatabaseReference splitgroupsDB;
    private FirebaseAuth auth;
    private FirebaseFunctions functions;


    private void initActivityUI(){
        getSupportActionBar().setTitle("Create Event");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        input_eventname = (EditText) findViewById(R.id.input_eventname);
        button_addmember = (TextView) findViewById(R.id.button_addmember);
        input_username_query = (EditText) findViewById(R.id.input_username_query);
        button_createevent = (Button) findViewById(R.id.button_createevent);
        recyclerView_members = (RecyclerView) findViewById(R.id.recyclerview_selected_members);

        membersAdapter = new SelectedMembersAdapter(members,CreateEvent.this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false);
        recyclerView_members.setLayoutManager(linearLayoutManager);
        recyclerView_members.setAdapter(membersAdapter);

        progressBar = (ProgressBar) findViewById(R.id.progressBar_creatingevent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        getSupportActionBar().setTitle("Create Split Event");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initActivityUI();

        db = FirebaseDatabase.getInstance().getReference().child("splitbit");
        splitgroupsDB = db.child("groupevents");
        auth = FirebaseAuth.getInstance();
        functions = FirebaseFunctions.getInstance();


        members.add(new User(auth.getCurrentUser().getDisplayName(),auth.getCurrentUser().getUid(),auth.getCurrentUser().getPhotoUrl().toString(),""));

        button_addmember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(input_username_query.getText().toString())){
                    addMember();
                }else{
                    input_username_query.setError("Enter Username");
                }
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
                                    //startActivity(new Intent(CreateEvent.this,EventRoom.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY));
                                    onBackPressed();
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

    private void addMember(){
        db.child("users").orderByChild("username").equalTo(input_username_query.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                        User user = snapshot.getValue(User.class);
                        if(user.getKey().equals(auth.getCurrentUser().getUid())){
                            Toast.makeText(CreateEvent.this, "You're group admin", Toast.LENGTH_SHORT).show();
                        }else if(member_meta.containsKey(user.getKey())){
                            Toast.makeText(CreateEvent.this, user.getName()+" is already a group member", Toast.LENGTH_SHORT).show();
                        }else{
                            members.add(user);
                            member_meta.put(user.getKey(),user.getName());
                            input_username_query.setText("");
                        }

                    }
                    //Toast.makeText(CreateEvent.this, dataSnapshot.child("name").getValue().toString(), Toast.LENGTH_SHORT).show();
                    membersAdapter.notifyDataSetChanged();
                }else{
                    Toast.makeText(CreateEvent.this, "User not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


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
