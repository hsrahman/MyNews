package com.example.hamidur.mynews;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by Naziur the greatest developer ever on 18/06/2017.
 */
public class SimpleFragmentPagerAdapter extends FragmentPagerAdapter {

    private String tabTitles [];

    public SimpleFragmentPagerAdapter(FragmentManager fm, List<String> sources) {
        super(fm);
        tabTitles = new String[sources.size()];
        for(int i = 0; i < sources.size(); i++){
            tabTitles[i] = sources.get(i);
        }
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new OptionOneFragment();
        } else if (position == 1){
            return new OptionTwoFragment();
        } else {
            return new OptionThreeFragment();
        }
    }

    @Override
    public int getCount() {
        return tabTitles.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }

}