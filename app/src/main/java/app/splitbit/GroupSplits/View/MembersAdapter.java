package app.splitbit.GroupSplits.View;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import app.splitbit.GroupSplits.AddTransaction;
import app.splitbit.GroupSplits.EventDetails;
import app.splitbit.GroupSplits.Model.Event;
import app.splitbit.GroupSplits.Model.Member;
import app.splitbit.R;

public class MembersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private ArrayList<Member> arraylist_members;
    private Context context;

    public MembersAdapter(ArrayList<Member> arraylist_members, Context context) {
        this.arraylist_members = arraylist_members;
        this.context = context;
    }

    public static class MemberViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        private TextView textView_username;
        private TextView textView_amount;
        private ImageView imageView_userphoto;
        public MemberViewHolder(View v) {
            super(v);
            textView_username = (TextView) v.findViewById(R.id.lim_username);
            textView_amount = (TextView) v.findViewById(R.id.lim_amount);
            //imageView_userphoto = (ImageView) v.findViewById(R.id.li)
        }
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_member, parent, false);
        return new MemberViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {

        final Member member = arraylist_members.get(position);
        if(member.getAmount() == 0){
            ((MemberViewHolder)holder).textView_amount.setText("None");
        }else{
            ((MemberViewHolder)holder).textView_amount.setText(member.getAmount()+" Rs.");
        }

        ((MemberViewHolder)holder).textView_username.setText(member.getName());

        ((MemberViewHolder)holder).itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //context.startActivity(new Intent(context, AddTransaction.class).putExtra("key",));
            }
        });


    }

    @Override
    public int getItemCount() {
        return arraylist_members.size();
    }
}
