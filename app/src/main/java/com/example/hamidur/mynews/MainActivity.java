package com.example.hamidur.mynews;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.hamidur.mynews.fragment.MainFragmentPagerAdapter;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find the view pager that will allow the user to swipe between fragments
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);

        // Create an adapter that knows which fragment should be shown on each page
        MainFragmentPagerAdapter adapter = new MainFragmentPagerAdapter(getSupportFragmentManager());

        //if (adapter.getCount() ==  0) ((TextView) findViewById(R.id.empty_view)).setText(R.string.no_sources_selected);

        // Set the adapter onto the view pager
        viewPager.setAdapter(adapter);

    }



}
