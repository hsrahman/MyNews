package com.example.hamidur.mynews;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {

    private SettingsAdapter settingsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        List<String> settingsOption = new ArrayList<>();
        settingsOption.add("Change Sources");
        settingsOption.add("My Source");
        settingsOption.add("Clear All Sources");


        settingsAdapter = new SettingsAdapter(this, settingsOption);

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
            case "Clear All Sources" :
                createPrefDialouge();
                break;

        }
    }

    private void createPrefDialouge () {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(SettingsActivity.this);
        builder1.setMessage("Do you want to remove all your currently selected sources?");
        builder1.setCancelable(true);
        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("my_sources", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.remove("source");
                        editor.clear();
                        editor.commit();
                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

       builder1.create().show();
    }
}
