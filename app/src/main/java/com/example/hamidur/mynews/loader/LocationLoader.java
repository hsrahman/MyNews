package com.example.hamidur.mynews.loader;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.example.hamidur.mynews.utility.QueryUtils;
import com.example.hamidur.mynews.model.Location;

import java.util.List;

/**
 * Created by pro-developer Hamidur on 27/06/2017.
 */
public class LocationLoader extends AsyncTaskLoader<List<Location>> {

    private String mUrl;

    public LocationLoader(Context context, String mUrl) {
        super(context);
        this.mUrl = mUrl;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<Location> loadInBackground() {
        if (mUrl == null) {
            return null;
        }
        return QueryUtils.fetchLocationData(mUrl);
    }
}
