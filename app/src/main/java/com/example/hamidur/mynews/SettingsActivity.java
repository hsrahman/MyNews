package com.example.hamidur.mynews;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {

    private SettingsAdapter settingsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        List<String> settingsOption = new ArrayList<>();
        settingsOption.add("Change Sources");
        settingsOption.add("My Source");
        settingsOption.add("Weather Settings");
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
                moveToActivity(SourceActivity.class);
                break;
            case "My Source" :
                moveToActivity(MySourcesActivity.class);
                break;
            case "Weather Settings" :
                moveToActivity(WeatherSettingsActivity.class);
                break;
            case "Clear All Sources" :
                createPrefDialouge();
                break;

        }
    }

    private void moveToActivity(Class<?> cls){
        Intent activtyToGoTo = new Intent(SettingsActivity.this, cls);
        startActivity(activtyToGoTo);
    }

    private void createPrefDialouge () {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(SettingsActivity.this);
        builder1.setMessage(getResources().getString(R.string.remove_all_sources));
        builder1.setCancelable(true);
        builder1.setPositiveButton(
                getResources().getString(R.string.yes),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("my_sources", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.remove(getResources().getString(R.string.source_pref));
                        editor.clear();
                        editor.commit();
                    }
                });

        builder1.setNegativeButton(
                getResources().getString(R.string.no),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

       builder1.create().show();
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

    private class SettingsAdapter extends ArrayAdapter<String> {


        public SettingsAdapter(Context context, List<String> settings) {
            super(context, 0, settings);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View listItemView = convertView;
            if (listItemView == null) {
                listItemView = LayoutInflater.from(getContext()).inflate(
                        R.layout.settings_items, parent, false);
            }

            ImageView settingIcon = (ImageView) listItemView.findViewById(R.id.setting_icon);
            TextView settingsText = (TextView) listItemView.findViewById(R.id.setting_text);

            settingsText.setText(getItem(position));
            settingIcon.setImageResource(getSettingResourceId(getItem(position)));
            return listItemView;
        }

        private int getSettingResourceId(String option){
            switch(option){
                case "Change Sources":
                    return R.drawable.ic_add_circle;
                case "My Source":
                    return R.drawable.ic_border_color;
                case "Weather Settings":
                    return R.drawable.ic_weather;
                case "Clear All Sources":
                    return R.drawable.ic_delete_sweep;
                default: return R.id.setting_icon;
            }
        }
    }
}
