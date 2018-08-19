package com.example.hamidur.mynews;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.hamidur.mynews.fragment.MainFragment;
import com.example.hamidur.mynews.fragment.MainFragmentPagerAdapter;
import com.example.hamidur.mynews.fragment.SearchFragment;

public class MainActivity extends AppCompatActivity implements SearchFragment.OnBackPressedListener{
    private  ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find the view pager that will allow the user to swipe between fragments
        viewPager = (ViewPager) findViewById(R.id.viewpager);

        // Create an adapter that knows which fragment should be shown on each page
        final MainFragmentPagerAdapter adapter = new MainFragmentPagerAdapter(getSupportFragmentManager(), this);

        // Set the adapter onto the view pager
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        if ( viewPager != null && viewPager.getCurrentItem() != 0 ) {
            viewPager.setCurrentItem(0);
        } else {
            finish();
        }
    }
}
