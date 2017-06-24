package com.example.hamidur.mynews;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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
                Toast.makeText(MainActivity.this, "Open weather view", Toast.LENGTH_LONG).show();
            }
        });

        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Open about view", Toast.LENGTH_LONG).show();
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
