package com.example.hamidur.mynews;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;

public class WeatherSettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_settings);

        Switch locSwitch = (Switch) findViewById(R.id.location_switch);

        locSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("my_sources", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean(getResources().getString(R.string.location_switch_pref), isChecked);
                editor.commit();
            }
        });

        RelativeLayout weatherSearch = (RelativeLayout) findViewById(R.id.weather_search);
        weatherSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent locationActivty = new Intent(WeatherSettingsActivity.this, LocationActivity.class);
                startActivity(locationActivty);
            }
        });

    }
}
