package dee.wallet;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by Dee on 2017/11/8.
 *
 */

public class WalletViewPagerAdapter extends FragmentPagerAdapter {
    private ArrayList<WalletFragment> fragments = new ArrayList<>();
    private WalletFragment currentFragment;
    private int year;
    private int month;

    public WalletViewPagerAdapter(FragmentManager fm,int year,int month) {
        super(fm);
        fragments.clear();
        fragments.add(WalletFragment.newInstance(0,year,month));
        fragments.add(WalletFragment.newInstance(1,year,month));
        fragments.add(WalletFragment.newInstance(2,year,month));
        fragments.add(WalletFragment.newInstance(3,year,month));
        this.year = year;
        this.month = month;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        if (getCurrentFragment() != object) {
            currentFragment = ((WalletFragment) object);
        }
        super.setPrimaryItem(container, position, object);
    }

    public WalletFragment getCurrentFragment(){
        return currentFragment;
    }

    public void updateSettingFragment(){
        fragments.set(3,WalletFragment.newInstance(3,year,month));
        notifyDataSetChanged();
    }

    public void updateHistoryFragment(){
        fragments.set(2,WalletFragment.newInstance(2,year,month));
        notifyDataSetChanged();
    }

    public void updateDetailFragment(){
        fragments.set(0,WalletFragment.newInstance(0,year,month));
        notifyDataSetChanged();
    }
}
