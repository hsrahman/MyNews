package com.example.hamidur.mynews;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {

    private ArrayAdapter <String> settingsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        List<String> settingsOption = new ArrayList<>();
        settingsOption.add("Change Sources");
        settingsOption.add("My Source");

        settingsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, settingsOption);

        ListView settingsView = (ListView) findViewById(R.id.settings_view);
        settingsView.setAdapter(settingsAdapter);

        settingsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openIntentByOption(settingsAdapter.getItem(position));
            }
        });
    }

    public void openIntentByOption (String option) {
        switch (option) {
            case "Change Sources" :
                Intent sourceActivity = new Intent(SettingsActivity.this, SourceActivity.class);
                startActivity(sourceActivity);
                break;
            case "My Source" :
                Intent orderByActivity = new Intent(SettingsActivity.this, OrderByActivity.class);
                startActivity(orderByActivity);
                break;

        }
    }
}
