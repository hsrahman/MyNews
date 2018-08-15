package com.example.hamidur.mynews.fragment;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.hamidur.mynews.AboutActivity;
import com.example.hamidur.mynews.HeadlineActivity;
import com.example.hamidur.mynews.NewsActivity;
import com.example.hamidur.mynews.R;
import com.example.hamidur.mynews.SettingsActivity;
import com.example.hamidur.mynews.WeatherActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {


    public MainFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        ImageView headline = (ImageView) rootView.findViewById(R.id.headline);
        ImageView news = (ImageView) rootView.findViewById(R.id.news);
        ImageView settings = (ImageView) rootView.findViewById(R.id.settings);
        ImageView weather = (ImageView) rootView.findViewById(R.id.weather);
        ImageView about = (ImageView) rootView.findViewById(R.id.about);
        ImageView newsApi = (ImageView) rootView.findViewById(R.id.news_api);
        ImageView weatherApi = (ImageView) rootView.findViewById(R.id.weather_api);
        ImageView currencyApi = (ImageView) rootView.findViewById(R.id.currency);
        ImageView geonamesApi = (ImageView) rootView.findViewById(R.id.geonames_api);
        ImageView exchangerateApi = (ImageView) rootView.findViewById(R.id.exchangerate_api);

        headline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent headlineActivity = new Intent(getActivity(), HeadlineActivity.class);
                startActivity(headlineActivity);
            }
        });

        news.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newsActivity = new Intent(getActivity(), NewsActivity.class);
                startActivity(newsActivity);
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settingsActivity = new Intent(getActivity(), SettingsActivity.class);
                startActivity(settingsActivity);
            }
        });

        weather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), WeatherActivity.class));
            }
        });

        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent aboutActivity = new Intent(getActivity(), AboutActivity.class);

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
                Toast.makeText(getActivity(), "Exchange Rates coming soon...", Toast.LENGTH_LONG).show();
            }
        });

        return rootView;
    }

}
