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

import com.google.gson.Gson;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageView headline = (ImageView) findViewById(R.id.headline);
        ImageView news = (ImageView) findViewById(R.id.news);
        ImageView settings = (ImageView) findViewById(R.id.settings);
        ImageView weather = (ImageView) findViewById(R.id.weather);
        ImageView about = (ImageView) findViewById(R.id.about);
        ImageView newsApi = (ImageView) findViewById(R.id.news_api);
        ImageView weatherApi = (ImageView) findViewById(R.id.weather_api);
        ImageView currencyApi = (ImageView) findViewById(R.id.currency);
        ImageView geonamesApi = (ImageView) findViewById(R.id.geonames_api);
        ImageView exchangerateApi = (ImageView) findViewById(R.id.exchangerate_api);
        headline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent headlineActivity = new Intent(MainActivity.this, HeadlineActivity.class);
                startActivity(headlineActivity);
            }
        });

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
                startActivity(new Intent(MainActivity.this, WeatherActivity.class));
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

        geonamesApi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri newsUri = Uri.parse("http://www.geonames.org/");
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, newsUri);
                startActivity(websiteIntent);
            }
        });

        exchangerateApi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri newsUri = Uri.parse("https://www.exchangerate-api.com/");
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
