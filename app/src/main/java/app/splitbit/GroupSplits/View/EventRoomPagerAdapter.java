package app.splitbit.GroupSplits.View;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import app.splitbit.Dummies.Settlements;


public class EventRoomPagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public EventRoomPagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        Settlements settlements = new Settlements();
        switch (position) {

            case 0:
                return settlements;
            case 1:
                return settlements;
            case 2:
                return settlements;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
