package app.splitbit;

import androidx.annotation.NonNull;
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

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import app.splitbit.GroupSplits.Create.CreateEvent;
import app.splitbit.GroupSplits.Create.SelectMembers;
import app.splitbit.GroupSplits.Model.Event;
import app.splitbit.GroupSplits.View.EventsAdapter;
import app.splitbit.Profile.Profile;
import app.splitbit.Settings.Settings;

public class Splitbit extends AppCompatActivity {

    //Android UI
    private RecyclerView recyclerview_events;
    private ArrayList<Event> arraylist_events;
    private ArrayList<Event> temp_event_list;
    private EventsAdapter eventsAdapter;
    private LinearLayout loadingEventsLayout;
    private LinearLayout noTransactionLayout;

    private LinearLayoutManager layoutManager;

    //Firebase
    private DatabaseReference db;
    private DatabaseReference eventsRef;
    private ValueEventListener valueEventListener;

    private FirebaseAuth auth;
    private GoogleSignInClient mGoogleSignInClient;
    private int mEvents = 13;
    private int i = 0;


    private void initActivityUI(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        loadingEventsLayout = (LinearLayout) findViewById(R.id.loadingEventsLayout);
        noTransactionLayout = (LinearLayout) findViewById(R.id.asplit_layout_notransaction);

        recyclerview_events = (RecyclerView) findViewById(R.id.recyclerview_groupevents);
        recyclerview_events.setNestedScrollingEnabled(false);
        arraylist_events = new ArrayList<>();
        temp_event_list = new ArrayList<>();
        eventsAdapter = new EventsAdapter(arraylist_events,Splitbit.this);
        layoutManager = new LinearLayoutManager(this);
        recyclerview_events.setLayoutManager(layoutManager);
        recyclerview_events.setAdapter(eventsAdapter);

        recyclerview_events.setNestedScrollingEnabled(false);

    }

    public void loadMore(View view){
        loadMoreEvents(eventsAdapter.getLastItemTimestamp());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spitbit);

        auth = FirebaseAuth.getInstance();

        db = FirebaseDatabase.getInstance().getReference().child("splitbit");
        eventsRef = db.child("usereventlist").child(auth.getCurrentUser().getUid());

        initActivityUI();
        loadMoreEvents(0);


        /*recyclerview_events.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int id = layoutManager.findLastCompletelyVisibleItemPosition();
                if(id>=mEvents-1){
                    loadMoreEvents(eventsAdapter.getLastItemTimestamp());
                }

            }
        });*/

    }

    /*private void initEventsList(){

        valueEventListener = eventsRef.limitToLast(mEvents).addValueEventListener(new ValueEventListener() {
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
                                    temp_event_list.add(event);
                                }
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
    }*/

    private void loadMoreEvents(long start){
        Query query;
        Log.d("Start From ",""+start);
        if(start > 0){
            query = eventsRef.orderByValue().startAt(start).limitToFirst(15);
        }else{
            query = eventsRef.orderByValue().limitToFirst(15);
        }

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                        //-- Got the added child
                        i++;
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

            /*case R.id.item_revoke_access:

                // Google revoke access
                mGoogleSignInClient.revokeAccess().addOnCompleteListener(this,
                        new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                auth.signOut();
                                startActivity(new Intent(Splitbit.this, Signin.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                            }
                        });

                return true;*/

            case R.id.item_settings:
                startActivity(new Intent(Splitbit.this, Settings.class));
                return true;


            case R.id.item_add_event:
                startActivity(new Intent(Splitbit.this, SelectMembers.class));
                return true;

            case R.id.item_profile:
                startActivity(new Intent(Splitbit.this, Profile.class));
                return true;


        }
        return super.onOptionsItemSelected(item);
    }

    public ArrayList<Event> reverse(ArrayList<Event> list) {

        for (int i = 0; i < list.size() / 2; i++) {
            Event temp = list.get(i);
            list.set(i, list.get(list.size() - i - 1));
            list.set(list.size() - i - 1, temp);
        }

        return list;
    }

    @Override
    protected void onResume() {
        super.onResume();
        //eventsRef.addValueEventListener(valueEventListener);

        //-- Making notification tray empty
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //eventsRef.removeEventListener(valueEventListener);
    }
}
