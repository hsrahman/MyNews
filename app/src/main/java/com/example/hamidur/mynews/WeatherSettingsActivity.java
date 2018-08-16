package com.example.hamidur.mynews;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hamidur.mynews.model.Location;
import com.google.gson.Gson;

public class WeatherSettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_settings);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Switch locSwitch = (Switch) findViewById(R.id.location_switch);

        locSwitch.setChecked(getSharedPreferences("my_sources", Context.MODE_PRIVATE).getBoolean(getResources().getString(R.string.location_switch_pref), true));

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
                Intent locationActivity = new Intent(WeatherSettingsActivity.this, LocationActivity.class);
                startActivity(locationActivity);
            }
        });

        LinearLayout defaultLoc = (LinearLayout) findViewById(R.id.default_location);
        defaultLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(WeatherSettingsActivity.this, "This is your default location", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        setDefaultLocation ();
    }

    private void setDefaultLocation () {
        Gson gson = new Gson();
        String locStrObj = getSharedPreferences("my_sources", Context.MODE_PRIVATE).getString(getResources().getString(R.string.location_pref), "");
        if (!locStrObj.equals("")) {
            Location myLoc = gson.fromJson(locStrObj, Location.class);

            TextView asciiName = (TextView) findViewById(R.id.location_ascii_name);
            asciiName.setText(myLoc.getAsciiName());

            TextView latLng = (TextView) findViewById(R.id.location_lat_lng);
            latLng.setText("Lat: " + myLoc.getLat() + ", Lng: " + myLoc.getLng());

            TextView timeZone = (TextView) findViewById(R.id.location_timezone);
            timeZone.setText(myLoc.getTimeZoneId());
        }
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
