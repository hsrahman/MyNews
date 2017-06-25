package com.example.hamidur.mynews;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

/**
 * Created by pro-developer Hamidur on 25/06/2017.
 */
public class WeatherLoader extends AsyncTaskLoader<List<Weather>> {

    private String mUrl;

    public WeatherLoader (Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<Weather> loadInBackground() {
        if (mUrl == null) return null;
        // Perform the network request, parse the response, and extract a list of earthquakes.
        List<Weather> allWeathers = QueryUtils.fetchWeatherData(mUrl);
        return allWeathers;
    }

}
