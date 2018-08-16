package com.example.hamidur.mynews;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.v4.util.ArraySet;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.hamidur.mynews.fragment.SimpleFragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;


public class NewsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Find the view pager that will allow the user to swipe between fragments
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);

        List<String> sources = new ArrayList<String>();

        for(String s : getApplicationContext().getSharedPreferences("my_sources", Context.MODE_PRIVATE).getStringSet(getResources().getString(R.string.source_pref), new ArraySet<String>())){
            sources.add(s);
        }

        // Create an adapter that knows which fragment should be shown on each page
        SimpleFragmentPagerAdapter adapter = new SimpleFragmentPagerAdapter(getSupportFragmentManager(), sources);

        if (adapter.getCount() ==  0) ((TextView) findViewById(R.id.empty_view)).setText(R.string.no_sources_selected);
        int limit = (adapter.getCount() > 1 ? adapter.getCount() - 1 : 1);

        // Set the adapter onto the view pager
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(limit);
        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home :
                finish();
                break;

            default:
                break;
        }

        return true;
    }
}
