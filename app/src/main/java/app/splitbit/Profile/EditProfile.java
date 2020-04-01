package app.splitbit.Profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import app.splitbit.R;

public class EditProfile extends AppCompatActivity {

    //-- UI
    private EditText editText_name;
    private EditText editText_email;
    private EditText editText_username;

    private TextView textView_username_status;

    private Button button_saveChanges;
    private Button button_cancel;

    private ProgressBar progressBar;

    //-- Strings and booleans
    private String currunt_username = "";
    private boolean setNewUsername = true;
    private String[] username;

    //-- Firebase
    private DatabaseReference db;
    private FirebaseAuth auth;


    //-- Init UI
    private void initActivityUI(){
        getSupportActionBar().setTitle("Edit Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        editText_name = (EditText) findViewById(R.id.edittext_ep_name);
        editText_email = (EditText) findViewById(R.id.edittext_ep_email);
        editText_username = (EditText) findViewById(R.id.edittext_ep_username);

        button_saveChanges = (Button) findViewById(R.id.button_ep_savechanges);
        button_cancel = (Button) findViewById(R.id.button_ep_cancel);

        textView_username_status = (TextView) findViewById(R.id.textView_username_validation_status);

        progressBar = (ProgressBar) findViewById(R.id.progressBar_savingchanges);

        username = new String[1];

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        //-- Strings
        currunt_username = getIntent().getStringExtra("username");

        //-- Init Activity UI
        initActivityUI();

        //-- Firebase
        db = FirebaseDatabase.getInstance().getReference().child("splitbit");
        auth = FirebaseAuth.getInstance();



        //-- Load Profile
        loadProfile();

        editText_username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(editText_username.getText().toString().equals(currunt_username)){
                    button_saveChanges.setEnabled(false);
                    //Toast.makeText(EditProfile.this, "true", Toast.LENGTH_SHORT).show();
                }else{
                    button_saveChanges.setEnabled(true);
                    //Toast.makeText(EditProfile.this, "false", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        button_saveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vaidateUsername();
            }
        });

        button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    private void vaidateUsername(){
        progressBar.setVisibility(View.VISIBLE);
        setControlsEnable(false);
        setNewUsername = true;
        //-- Set Username routine
        if(!TextUtils.isEmpty(editText_username.getText().toString())){

            //-- Check if entered username is exists or not
            db.child("usernames").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    int i = 0;
                    if(dataSnapshot.exists()){

                        //-- Going through al usernames
                        for(DataSnapshot username:dataSnapshot.getChildren()){
                            i++;

                            //-- Username Exists
                            if(username.getValue().toString().equals(editText_username.getText().toString())){
                                setNewUsername = false;
                                progressBar.setVisibility(View.INVISIBLE);
                                setControlsEnable(true);
                                textView_username_status.setVisibility(View.VISIBLE);
                            }else{
                                //-- Username not exists
                                if(i == dataSnapshot.getChildrenCount() && setNewUsername){

                                    //-- Register the username
                                    db.child("usernames").child(auth.getCurrentUser().getUid()).setValue(editText_username.getText().toString())
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                //-- Username Registered
                                                //-- Update User's Username
                                                db.child("users").child(auth.getCurrentUser().getUid()).child("username").setValue(editText_username.getText().toString())
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                //-- update to userinfo succeed

                                                                progressBar.setVisibility(View.INVISIBLE);
                                                                textView_username_status.setVisibility(View.GONE);
                                                                setControlsEnable(true);
                                                                onBackPressed();

                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                //-- Update to user info failed
                                                                db.child("usernames").child(auth.getCurrentUser().getUid()).setValue(currunt_username);
                                                                Toast.makeText(EditProfile.this, "Username not updated! try again", Toast.LENGTH_SHORT).show();
                                                                onBackPressed();
                                                            }
                                                        });
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(EditProfile.this, "Oops! Something went wrong", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                }else{

                                }
                            }


                        }

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    progressBar.setVisibility(View.INVISIBLE);
                    textView_username_status.setVisibility(View.GONE);
                    setControlsEnable(true);
                    Toast.makeText(EditProfile.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                }
            });

        }else{

        }
    }

    private void loadProfile(){

        db.child("users").child(auth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    editText_name.setText(dataSnapshot.child("name").getValue().toString());
                    editText_email.setText(dataSnapshot.child("email").getValue().toString());
                    if(dataSnapshot.child("username").exists()){
                        editText_username.setText(dataSnapshot.child("username").getValue().toString());
                        username[0] = dataSnapshot.child("username").getValue().toString();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void setControlsEnable(boolean state){
        button_saveChanges.setEnabled(state);
        button_cancel.setEnabled(state);
        editText_username.setEnabled(state);
    }

    public void setCurrunt_username(String currunt_username) {
        this.currunt_username = currunt_username;
    }

    public String getCurrunt_username() {
        return currunt_username;
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
