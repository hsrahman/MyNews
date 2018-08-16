package com.example.hamidur.mynews;

import android.content.Context;
import android.support.v4.util.ArraySet;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;

import com.example.hamidur.mynews.adapter.SourceAdapter;
import com.example.hamidur.mynews.model.Source;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MySourcesActivity extends AppCompatActivity {

    private SourceAdapter mAdapter;
    private final Gson gson = new Gson();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_sources);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        ListView orderViewList = (ListView) findViewById(R.id.order_by_list);
        List<Source> options = new ArrayList<>();
        Set<String> prefSource = getSharedPreferences("my_sources", Context.MODE_PRIVATE).getStringSet(getResources().getString(R.string.source_pref), new ArraySet<String>());

        //((TextView) findViewById(R.id.empty_view)).setText(R.string.no_sources_selected);

        for(String s : prefSource){
            Source source = gson.fromJson(s, Source.class);
            options.add(source);
        }

        mAdapter = new SourceAdapter(this, options);

        orderViewList.setEmptyView(findViewById(R.id.empty_view));

        orderViewList.setAdapter(mAdapter);

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
