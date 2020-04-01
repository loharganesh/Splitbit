package app.splitbit.GroupSplits;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import app.splitbit.GroupSplits.Model.Member;
import app.splitbit.GroupSplits.View.EventRoomPagerAdapter;
import app.splitbit.GroupSplits.View.MembersAdapter;
import app.splitbit.R;

public class EventDetails extends AppCompatActivity {

    //-- Strings
    private String EVENT_ID;
    private String ADMIN;

    //--UI
    private TextView textView_eventname;
    private TextView textView_totalbill;


    private CollapsingToolbarLayout collapsingToolbarLayout;

    private ViewPager viewPager;
    private DatabaseReference db;

    private void initActivityUI(){

        //-- Getting String Extras
        EVENT_ID = getIntent().getStringExtra("key");
        ADMIN = getIntent().getStringExtra("admin");


        //-- UI
        textView_eventname = (TextView) findViewById(R.id.textview_ed_eventname);
        textView_totalbill = (TextView) findViewById(R.id.textview_ed_totalbill);


        //-- Firebase
        db = FirebaseDatabase.getInstance().getReference().child("splitbit");


        //-- Setting up Collapsing Toolbar -------------------------------------------------------------------------

        collapsingToolbarLayout = findViewById(R.id.collapsingToolbar);
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appBarLayout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbarLayout.setTitle("Event Details");
                    collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.ExpandedAppBarTextStyle);
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbarLayout.setTitle("");
                    isShow = false;
                }
            }
        });

        //-----------------------------------------------------------------------------------------------------------------------------

        //-- Getting Event Info
        getEventInfo();


        //-- Setting Up Tab layout -----------------------------------------------------------------------------------------------------

        TabLayout tabLayout = (TabLayout) findViewById(R.id.event_tab_layout);

        tabLayout.addTab(tabLayout.newTab().setText("Settlements"));
        tabLayout.addTab(tabLayout.newTab().setText("Members"));
        tabLayout.addTab(tabLayout.newTab().setText("Transactions"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);



        viewPager = (ViewPager) findViewById(R.id.viewPager);
        //-- Parameters for fragments
        HashMap<String,Object> params = new HashMap<>();
        params.put("key",EVENT_ID);
        params.put("admin",ADMIN);
        final PagerAdapter adapter = new EventRoomPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount(),params);
        viewPager.setAdapter(adapter);

        /* the ViewPager requires a minimum of 1 as OffscreenPageLimit */
        int limit = (adapter.getCount() > 1 ? adapter.getCount() - 1 : 1);

        viewPager.setOffscreenPageLimit(limit);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        //-------------------------------------------------------------------------------------------------------------------------------

    }


    //--
    private void getEventInfo(){

        //Getting Event Room Information
        db.child("groupevents").child(EVENT_ID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String event_name = dataSnapshot.child("eventname").getValue().toString();

                final Animation fadeIn = new AlphaAnimation(0.0f, 1.0f);
                fadeIn.setDuration(1000);
                fadeIn.setStartOffset(0);
                textView_eventname.setText(event_name);
                textView_eventname.startAnimation(fadeIn);

                db.child("payers").child(EVENT_ID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        int total = 0;
                        for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                            total += (long)snapshot.getValue();
                        }
                        textView_totalbill.setText(total+" Rs.");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void addNewTransaction(View view){
        startActivity(new Intent(EventDetails.this,AddTransaction.class).putExtra("key",EVENT_ID));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        initActivityUI();

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.event_room_menu, menu);
        return true;
    }

}
