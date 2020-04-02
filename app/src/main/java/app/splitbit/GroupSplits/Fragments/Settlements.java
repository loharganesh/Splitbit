package app.splitbit.GroupSplits.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import app.splitbit.GroupSplits.EventRoom;
import app.splitbit.GroupSplits.Model.Settlement;
import app.splitbit.GroupSplits.View.SettlementsAdapter;
import app.splitbit.R;

public class Settlements extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    //-- UI
    private RecyclerView recyclerview_settlements;
    private ArrayList<Settlement> arraylist_settlements;
    private SettlementsAdapter settlementsAdapter;
    private LinearLayout noSettements;

    //-- Strings
    private String EVENT_ROOM_KEY;

    //-- Firebase
    private DatabaseReference db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settlements, container, false);

        //-- String
        EVENT_ROOM_KEY = getArguments().getString("key");

        //-- Firebase
        db = FirebaseDatabase.getInstance().getReference().child("splitbit");

        //-- UI

        noSettements = (LinearLayout) view.findViewById(R.id.noSettlementsLayout);

        recyclerview_settlements = (RecyclerView) view.findViewById(R.id.recycerview_settlements);
        arraylist_settlements = new ArrayList<>();
        settlementsAdapter = new SettlementsAdapter(arraylist_settlements, Settlements.this.getContext());

        recyclerview_settlements.setLayoutManager(new LinearLayoutManager(Settlements.this.getContext()));
        recyclerview_settlements.setAdapter(settlementsAdapter);

        loadSettlements();

        return view;
    }


    private void loadSettlements(){
        db.child("settlements/"+EVENT_ROOM_KEY).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                arraylist_settlements.clear();
                if(dataSnapshot.exists()) {

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                        final Settlement settlement = new Settlement();

                        for(final DataSnapshot member : snapshot.getChildren()) {

                            if (member.getValue().toString().equals("0")) {
                                settlement.setReciever(member.getKey());
                            } else {
                                settlement.setPayer(member.getKey());
                                settlement.setAmount(member.getValue().toString());
                            }

                        }

                        if( !TextUtils.isEmpty(settlement.getReciever()) && !TextUtils.isEmpty(settlement.getPayer()) ){
                            arraylist_settlements.add(settlement);
                        }
                        settlementsAdapter.notifyDataSetChanged();
                        if(arraylist_settlements.size() > 0){
                            noSettements.setVisibility(View.GONE);
                        }
                    }
                }else{
                    noSettements.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onRefresh() {
        loadSettlements();
    }
}
