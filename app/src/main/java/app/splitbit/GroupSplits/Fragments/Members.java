package app.splitbit.GroupSplits.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import app.splitbit.GroupSplits.EventDetails;
import app.splitbit.GroupSplits.Model.Member;
import app.splitbit.GroupSplits.View.MembersAdapter;
import app.splitbit.R;

public class Members extends Fragment {


    //-- String
    private String EVENT_ID;
    private String ADMIN;

    //-- Firebase
    private DatabaseReference db;
    private DatabaseReference memberRef;
    private FirebaseAuth auth;
    private ChildEventListener childEventListener;

    //-- UI
    private RecyclerView recyclerView_memebers;
    private MembersAdapter membersAdapter;
    private ArrayList<Member> arrayList_members;

    //-- Hashmap
    HashMap<String,Object> members_map;
    


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_members, container, false);

        EVENT_ID = getArguments().getString("key");
        ADMIN = getArguments().getString("admin");

        //-- Firebase
        db = FirebaseDatabase.getInstance().getReference("splitbit");
        memberRef = db.child("payers").child(EVENT_ID);
        auth = FirebaseAuth.getInstance();

        //-- UI
        recyclerView_memebers = (RecyclerView) view.findViewById(R.id.recyclerview_members);
        arrayList_members = new ArrayList<>();

        members_map = new HashMap<>();

        membersAdapter = new MembersAdapter(arrayList_members, Members.this.getContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(Members.this.getContext(),LinearLayoutManager.HORIZONTAL,false);
        recyclerView_memebers.setLayoutManager(layoutManager);
        recyclerView_memebers.setAdapter(membersAdapter);
        recyclerView_memebers.setNestedScrollingEnabled(false);
        ((SimpleItemAnimator) recyclerView_memebers.getItemAnimator()).setSupportsChangeAnimations(false);


        initMembers();
        addMemberListener();

        // Inflate the layout for this fragment
        return view;
    }

    private void initMembers(){
        db.child("payers").child(EVENT_ID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot snapshot:dataSnapshot.getChildren()){

                    final Member member = new Member();
                    member.setKey(snapshot.getKey());
                    member.setAmount((long)snapshot.getValue());
                    member.setName("");

                    //-- Adding into ArrayList
                    arrayList_members.add(member);
                    //-- Adding Into Map and saving index for item
                    members_map.put(member.getKey(),arrayList_members.indexOf(member));

                    db.child("users").child(member.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            arrayList_members.get((int)members_map.get(member.getKey())).setName(dataSnapshot.child("name").getValue().toString());
                            membersAdapter.notifyItemChanged((int)members_map.get(member.getKey()));
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                }
                membersAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void addMemberListener(){
        childEventListener = memberRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d("  Function Status : "," OnChildAdded()");

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                String changed_child_key = dataSnapshot.getKey();
                int child_position = (int)members_map.get(changed_child_key);
                arrayList_members.get(child_position).setAmount((long)dataSnapshot.getValue());
                membersAdapter.notifyItemChanged(child_position);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Log.d("  Function Status "," OnChildRemoved()");
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d("  Function Status "," OnChildMoved()");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        memberRef.addChildEventListener(childEventListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        memberRef.removeEventListener(childEventListener);
    }
}
