package app.splitbit.GroupSplits.View;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import app.splitbit.GroupSplits.Model.User;
import app.splitbit.R;

public class UserAdapter extends ArrayAdapter<User> {
    private Activity activity;
    private ArrayList<User> arraylist_user;
    private static LayoutInflater inflater = null;

    public UserAdapter(Activity activity, int textViewResourceId, ArrayList<User> arraylist_user) {
        super(activity, textViewResourceId, arraylist_user);
        try {
            this.activity = activity;
            this.arraylist_user = arraylist_user;

            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        } catch (Exception e) {

        }
    }

    public int getCount() {
        return arraylist_user.size();
    }

    public User getItem(User position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public static class ViewHolder {
        public TextView display_username;


    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        final ViewHolder holder;
        try {
            if (convertView == null) {
                vi = inflater.inflate(R.layout.list_item_user, null);
                holder = new ViewHolder();

                holder.display_username = (TextView) vi.findViewById(R.id.list_item_username);


                vi.setTag(holder);
            } else {
                holder = (ViewHolder) vi.getTag();
            }



            holder.display_username.setText(arraylist_user.get(position).getName());


        } catch (Exception e) {


        }
        return vi;
    }
}