package app.splitbit.GroupSplits.View;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import app.splitbit.GroupSplits.Model.User;
import app.splitbit.R;

public class SelectedMembersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private ArrayList<User> arraylist_members;
    private Context context;

    public SelectedMembersAdapter(ArrayList<User> arraylist_members, Context context) {
        this.arraylist_members = arraylist_members;
        this.context = context;
    }

    public static class SelectedMembersViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        private TextView textView_username;
        public SelectedMembersViewHolder(View v) {
            super(v);
            textView_username = (TextView) v.findViewById(R.id.lim_name);
        }
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_selected_member, parent, false);
        return new SelectedMembersViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        User user = arraylist_members.get(position);
        if(user.getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
            ((SelectedMembersViewHolder)holder).textView_username.setText("You");
        }else{
            ((SelectedMembersViewHolder)holder).textView_username.setText(user.getName());
        }

    }

    @Override
    public int getItemCount() {
        return arraylist_members.size();
    }
}
