package app.splitbit.GroupSplits;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
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
import com.google.firebase.database.snapshot.DoubleNode;

import java.util.ArrayList;

import app.splitbit.GroupSplits.Model.Settlement;
import app.splitbit.GroupSplits.View.SettlementsAdapter;
import app.splitbit.R;

public class EventRoom extends AppCompatActivity {

    private String EVENT_ROOM_KEY;
    private String EVENT_ADMIN;
    private String all_members = "";
    private ArrayList<String> members;

    private RecyclerView recyclerview_settlements;
    private ArrayList<Settlement> arraylist_settlements;
    private SettlementsAdapter settlementsAdapter;

    //UI
    private TextView textview_eventname;
    private TextView textview_memebers;
    private Button button_addtransaction;

    //Firebase
    private DatabaseReference db;
    private FirebaseAuth auth;

    private void initActivityUI(){
        getSupportActionBar().setTitle("Event");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //UI
        textview_eventname = (TextView) findViewById(R.id.textview_eventname);
        textview_memebers = (TextView) findViewById(R.id.textview_members);

        button_addtransaction = (Button) findViewById(R.id.button_addtransaction);

        recyclerview_settlements = (RecyclerView) findViewById(R.id.recycerview_settlements);
        arraylist_settlements = new ArrayList<>();
        settlementsAdapter = new SettlementsAdapter(arraylist_settlements,EventRoom.this);

        recyclerview_settlements.setLayoutManager(new LinearLayoutManager(this));
        recyclerview_settlements.setAdapter(settlementsAdapter);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_room);

        initActivityUI();

        EVENT_ROOM_KEY = getIntent().getStringExtra("key");
        EVENT_ADMIN = getIntent().getStringExtra("admin");



        members = new ArrayList<>();

        //Firebase
        db = FirebaseDatabase.getInstance().getReference().child("splitbit");
        auth = FirebaseAuth.getInstance();



        //Initializing Event Room
        setUpEventRoom();
        loadSettlements();

        if(EVENT_ADMIN.equals(auth.getCurrentUser().getUid())){
            button_addtransaction.setVisibility(View.VISIBLE);
        }else{
            button_addtransaction.setVisibility(View.GONE);
        }

        button_addtransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EventRoom.this,AddTransaction.class).putExtra("key",EVENT_ROOM_KEY));
            }
        });

    }

    private void setUpEventRoom(){

        //Getting Event Room Information
        db.child("groupevents").child(EVENT_ROOM_KEY).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String event_name = dataSnapshot.child("eventname").getValue().toString();
                textview_eventname.setText(event_name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //Getting Members Info
        db.child("groupeventsmembers").child(EVENT_ROOM_KEY).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snap:dataSnapshot.getChildren()){
                    String member = snap.getKey().toString();

                    db.child("users").child(member).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String name = dataSnapshot.child("name").getValue().toString();
                            if(all_members.equals("")){
                                all_members = all_members+name;
                            }else{
                                all_members = all_members+"  •  "+name;
                            }
                            textview_memebers.setText(all_members);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void loadSettlements(){
        db.child("settlements/"+EVENT_ROOM_KEY).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                        //Log.d("   Member",""+snapshot.getKey());

                        final Settlement settlement = new Settlement();

                        for(final DataSnapshot member : snapshot.getChildren()) {

                            if (member.getValue().toString().equals("0")) {
                                settlement.setReciever(member.getKey());
                            } else {
                                settlement.setPayer(member.getKey());
                                settlement.setAmount(member.getValue().toString());
                            }

                        }
                        arraylist_settlements.add(settlement);
                        settlementsAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.event_room_menu, menu);
        return true;
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
