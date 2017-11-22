package dee.wallet;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by Dee on 2017/11/8.
 *
 */

public class WalletViewPagerAdapter extends FragmentPagerAdapter {
    private ArrayList<WalletFragment> fragments = new ArrayList<>();
    private WalletFragment currentFragment;

    public WalletViewPagerAdapter(FragmentManager fm,int year,int month) {
        super(fm);
        fragments.clear();
        fragments.add(WalletFragment.newInstance(0,year,month));
        fragments.add(WalletFragment.newInstance(1,year,month));
        fragments.add(WalletFragment.newInstance(2,year,month));
        fragments.add(WalletFragment.newInstance(3,year,month));
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
}
