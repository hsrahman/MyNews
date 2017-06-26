package com.example.hamidur.mynews;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageView news = (ImageView) findViewById(R.id.news);
        ImageView settings = (ImageView) findViewById(R.id.settings);
        ImageView weather = (ImageView) findViewById(R.id.weather);
        ImageView about = (ImageView) findViewById(R.id.about);
        ImageView newsApi = (ImageView) findViewById(R.id.news_api);
        ImageView weatherApi = (ImageView) findViewById(R.id.weather_api);
        ImageView currencyApi = (ImageView) findViewById(R.id.currency);
        news.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newsActivity = new Intent(MainActivity.this, NewsActivity.class);
                startActivity(newsActivity);
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settingsActivity = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(settingsActivity);
            }
        });

        weather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int locationMode = 0;
                try {
                    locationMode = Settings.Secure.getInt(getApplicationContext().getContentResolver(), Settings.Secure.LOCATION_MODE);
                } catch (Settings.SettingNotFoundException e) {
                    e.printStackTrace();
                }

                if(locationMode == Settings.Secure.LOCATION_MODE_OFF) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
                    builder1.setMessage(getResources().getString(R.string.network_not_enabled));
                    builder1.setCancelable(true);
                    builder1.setPositiveButton(
                            getResources().getString(R.string.open_location_settings),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent myIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                    startActivity(myIntent);
                                }
                            });

                    builder1.setNegativeButton(
                            getResources().getString(R.string.Cancel),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Toast.makeText(getApplicationContext(), "To use weather feature you must enable location settings", Toast.LENGTH_SHORT).show();
                                    dialog.cancel();
                                }
                            });

                    builder1.create().show();

                } else {
                    Intent newsActivity = new Intent(MainActivity.this, WeatherActivity.class);
                    startActivity(newsActivity);
                }
            }
        });

        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent aboutActivity = new Intent(MainActivity.this, AboutActivity.class);

               /* ActivityOptions options = ActivityOptions.makeScaleUpAnimation(v, 0,
                        0, v.getWidth(), v.getHeight());*/

                startActivity(aboutActivity/*, options.toBundle()*/);
            }
        });

        newsApi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri newsUri = Uri.parse("https://newsapi.org/");
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, newsUri);
                startActivity(websiteIntent);
            }
        });

        weatherApi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri newsUri = Uri.parse("https://developer.weatherunlocked.com/");
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, newsUri);
                startActivity(websiteIntent);
            }
        });

        currencyApi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Exchange Rates coming soon...", Toast.LENGTH_LONG).show();
            }
        });

    }

}
