package app.splitbit.GroupSplits.View;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import app.splitbit.GroupSplits.EventRoom;
import app.splitbit.GroupSplits.Model.Event;
import app.splitbit.R;

public class EventsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private ArrayList<Event> arraylist_events;
    private Context context;

    public EventsAdapter(ArrayList<Event> arraylist_events, Context context) {
        this.arraylist_events = arraylist_events;
        this.context = context;
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        private TextView textView_event_name;
        public EventViewHolder(View v) {
            super(v);
            textView_event_name = (TextView) v.findViewById(R.id.textview_event_name);
        }
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_groupevent, parent, false);
        return new EventViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final Event event = arraylist_events.get(position);
        ((EventViewHolder)holder).textView_event_name.setText(event.getEventname());
        ((EventViewHolder)holder).itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, EventRoom.class).putExtra("key",event.getKey()).putExtra("admin",event.getEventadmin()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return arraylist_events.size();
    }
}
