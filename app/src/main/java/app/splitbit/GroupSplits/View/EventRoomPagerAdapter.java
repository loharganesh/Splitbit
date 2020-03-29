package app.splitbit.GroupSplits.View;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.HashMap;

import app.splitbit.GroupSplits.Fragments.Members;
import app.splitbit.GroupSplits.Fragments.Settlements;
import app.splitbit.GroupSplits.Fragments.Transactions;


public class EventRoomPagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;
    HashMap<String,Object> params;

    public EventRoomPagerAdapter(FragmentManager fm, int NumOfTabs, HashMap<String,Object> params) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.params = params;
    }

    @Override
    public Fragment getItem(int position) {
        Members members = new Members();
        Settlements settlements = new Settlements();
        Transactions transactions = new Transactions();

        //-- Parameters for fragment
        Bundle bundle = new Bundle();
        bundle.putString("key",params.get("key").toString());
        bundle.putString("admin",params.get("admin").toString());

        members.setArguments(bundle);
        transactions.setArguments(bundle);
        settlements.setArguments(bundle);

        switch (position) {

            case 0:
                return settlements;
            case 1:
                return members;
            case 2:
                return transactions;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
