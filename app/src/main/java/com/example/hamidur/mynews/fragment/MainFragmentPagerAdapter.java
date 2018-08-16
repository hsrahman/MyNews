package com.example.hamidur.mynews.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by Naziur on 15/08/2018.
 */

public class MainFragmentPagerAdapter extends FragmentPagerAdapter {

    SearchFragment.OnBackPressedListener onBackPressedListener;
    public MainFragmentPagerAdapter(FragmentManager fm, SearchFragment.OnBackPressedListener onBackPressedListener) {
        super(fm);
        this.onBackPressedListener = onBackPressedListener;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new MainFragment();
        } else {
            SearchFragment searchFragment = new SearchFragment();
            searchFragment.setOnBackPressedListener(onBackPressedListener);
            return searchFragment;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}
