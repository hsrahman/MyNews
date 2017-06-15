package com.example.hamidur.mynews;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;


/**
 * Created by pro-developer Hamidur on 15/06/2017.
 */
public class SourceLoader extends AsyncTaskLoader<List<Source>> {

    private String mUrl;

    public SourceLoader (Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<Source> loadInBackground() {
        if (mUrl == null) return null;
        // Perform the network request, parse the response, and extract a list of earthquakes.
        List<Source> sources = QueryUtils.fetchNewsArticleData(mUrl);
        return sources;
    }

}
