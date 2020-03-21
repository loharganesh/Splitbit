package app.splitbit.Profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import app.splitbit.R;

public class Profile extends AppCompatActivity {

    //--UI
    private TextView textView_name;
    private TextView textView_username;
    private Button button_edit_profile;

    //-- Firebase
    private DatabaseReference db;
    private FirebaseAuth auth;

    //-- Init UI
    private void initActivityUI(){
        getSupportActionBar().setTitle("Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        textView_name = (TextView)  findViewById(R.id.textview_profile_name);
        textView_username = (TextView) findViewById(R.id.textview_profile_username);
        button_edit_profile = (Button) findViewById(R.id.button_editprofile);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //--UI
        initActivityUI();

        //-- Firebase
        db = FirebaseDatabase.getInstance().getReference().child("splitbit");
        auth = FirebaseAuth.getInstance();

        //--  Load Profile
        loadProfile();

        button_edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Profile.this,EditProfile.class).putExtra("username",textView_username.getText().toString()));
            }
        });
    }

    private void loadProfile(){
        db.child("users").child(auth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    textView_name.setText(dataSnapshot.child("name").getValue().toString());
                    if(dataSnapshot.child("username").exists()){
                        textView_username.setText(dataSnapshot.child("username").getValue().toString());
                    }else{
                        textView_username.setText("Click on Edit Profile and set your username so your friends can find you on splitbit");
                    }

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
