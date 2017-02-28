package common;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import xujiantao.com.chuangwen.SuperAwesomeCardFragment;

public class CategoryPageAdapter extends FragmentPagerAdapter {

    private final String[] TITLES = {"全部", "初创期", "成长发展期", "上市公司", "成熟期"};

    public CategoryPageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return TITLES[position];
    }

    @Override
    public int getCount() {
        return TITLES.length;
    }

    @Override
    public Fragment getItem(int position) {
        return SuperAwesomeCardFragment.newInstance(position);
    }

}