package com.example.hamidur.mynews;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;

public class NewsActivity extends AppCompatActivity {

    private NewsAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        // Find a reference to the {@link ListView} in the layout
        ListView earthquakeListView = (ListView) findViewById(R.id.list);

        ArrayList<NewsArticle> articles = new ArrayList<NewsArticle>();
        articles.add(new NewsArticle("https://image.flaticon.com/teams/new/1-freepik.jpg", "test description", "Test title", "Mr Author", "08/07/1996"));


        // Create a new adapter that takes an empty list of earthquakes as input
        mAdapter = new NewsAdapter(this, articles);



        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        earthquakeListView.setAdapter(mAdapter);

    }
}
