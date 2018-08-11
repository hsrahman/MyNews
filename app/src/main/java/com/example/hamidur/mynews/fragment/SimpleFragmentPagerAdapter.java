package com.example.hamidur.mynews.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.hamidur.mynews.model.Source;
import com.google.gson.Gson;

import java.util.List;

/**
 * Created by Naziur the greatest developer ever on 18/06/2017.
 */
public class SimpleFragmentPagerAdapter extends FragmentPagerAdapter {

    private String tabTitles [];

    public SimpleFragmentPagerAdapter(FragmentManager fm, List<String> sources) {
        super(fm);
        tabTitles = new String[sources.size()];
        Gson gson = new Gson();
        for(int i = 0; i < sources.size(); i++){
            Source source = gson.fromJson(sources.get(i), Source.class);
            tabTitles[i] = source.getName();
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
