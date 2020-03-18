package app.splitbit.GroupSplits.View;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Set;

import app.splitbit.GroupSplits.EventRoom;
import app.splitbit.GroupSplits.Model.Event;
import app.splitbit.GroupSplits.Model.Settlement;
import app.splitbit.R;

public class SettlementsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private ArrayList<Settlement> arraylist_settlements;
    private Context context;

    public SettlementsAdapter(ArrayList<Settlement> arraylist_settlements, Context context) {
        this.arraylist_settlements = arraylist_settlements;
        this.context = context;
    }

    public static class SettlementViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        private TextView textView_event_name;
        public SettlementViewHolder(View v) {
            super(v);
            textView_event_name = (TextView) v.findViewById(R.id.textview_settlement);
        }
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_settlement, parent, false);
        return new SettlementViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        final Settlement settlement = arraylist_settlements.get(position);

        FirebaseDatabase.getInstance().getReference().child("splitbit/users/"+settlement.getPayer()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(final @NonNull DataSnapshot payerSnap) {
                FirebaseDatabase.getInstance().getReference().child("splitbit/users/"+settlement.getReciever()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot recrSnap) {
                        String payer_name = payerSnap.child("name").getValue().toString();
                        String recr_name = recrSnap.child("name").getValue().toString();
                        ((SettlementViewHolder)holder).textView_event_name.setText(payer_name+"   ----------->   "+recr_name+"  :  "+settlement.getAmount());
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

    @Override
    public int getItemCount() {
        return arraylist_settlements.size();
    }
}
