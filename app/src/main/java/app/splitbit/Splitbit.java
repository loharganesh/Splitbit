package app.splitbit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import app.splitbit.Authentication.Signin;
import app.splitbit.GroupSplits.CreateEvent;
import app.splitbit.GroupSplits.Model.Event;
import app.splitbit.GroupSplits.View.EventsAdapter;
import app.splitbit.Profile.Profile;

public class Splitbit extends AppCompatActivity {

    //Android UI
    private RecyclerView recyclerview_events;
    private ArrayList<Event> arraylist_events;
    private EventsAdapter eventsAdapter;
    private LinearLayout loadingEventsLayout;
    private LinearLayout noTransactionLayout;

    private LinearLayoutManager layoutManager;

    //Firebase
    private DatabaseReference db;
    private DatabaseReference eventsRef;
    private ChildEventListener childEventListener;

    private FirebaseAuth auth;
    private GoogleSignInClient mGoogleSignInClient;
    private int mEvents = 13;


    private void initActivityUI(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        loadingEventsLayout = (LinearLayout) findViewById(R.id.loadingEventsLayout);
        noTransactionLayout = (LinearLayout) findViewById(R.id.asplit_layout_notransaction);

        recyclerview_events = (RecyclerView) findViewById(R.id.recyclerview_groupevents);
        recyclerview_events.setNestedScrollingEnabled(false);
        arraylist_events = new ArrayList<>();
        eventsAdapter = new EventsAdapter(arraylist_events,Splitbit.this);
        layoutManager = new LinearLayoutManager(this);
        recyclerview_events.setLayoutManager(layoutManager);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerview_events.setAdapter(eventsAdapter);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spitbit);

        // [START config_signin]
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // [END config_signin]

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        auth = FirebaseAuth.getInstance();

        db = FirebaseDatabase.getInstance().getReference().child("splitbit");
        eventsRef = db.child("users").child(auth.getCurrentUser().getUid()).child("groupevents");

        initActivityUI();
        initEventsList();



    }

    private void initEventsList(){

        eventsRef.limitToFirst(mEvents).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                        //-- Got the added child
                        db.child("groupevents").child(snapshot.getKey()).orderByChild("timestamp").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                Event event = dataSnapshot.getValue(Event.class);
                                if(!arraylist_events.contains(event)){
                                    arraylist_events.add(event);
                                }
                                eventsAdapter.notifyDataSetChanged();
                                loadingEventsLayout.setVisibility(View.GONE);
                                noTransactionLayout.setVisibility(View.GONE);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                loadingEventsLayout.setVisibility(View.GONE);
                                noTransactionLayout.setVisibility(View.GONE);
                            }
                        });
                    }
                }else{
                    loadingEventsLayout.setVisibility(View.GONE);
                    noTransactionLayout.setVisibility(View.VISIBLE);
                }




            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void createSplitEvent(View view){
        startActivity(new Intent(Splitbit.this, CreateEvent.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {

            case R.id.item_revoke_access:

                // Google revoke access
                mGoogleSignInClient.revokeAccess().addOnCompleteListener(this,
                        new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                auth.signOut();
                                startActivity(new Intent(Splitbit.this, Signin.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                            }
                        });

                return true;

            case R.id.item_signout:
                auth.signOut();
                startActivity(new Intent(Splitbit.this, Signin.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                return true;


            case R.id.item_add_event:
                startActivity(new Intent(Splitbit.this, CreateEvent.class));
                return true;

            case R.id.item_profile:
                startActivity(new Intent(Splitbit.this, Profile.class));
                return true;


        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }
}
