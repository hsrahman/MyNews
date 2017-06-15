package com.example.hamidur.mynews;

import android.content.Context;
import android.content.AsyncTaskLoader;

import java.util.List;

/**
 * Created by pro-developer Hamidur on 13/06/2017.
 */
public class NewsLoader extends AsyncTaskLoader<List<NewsArticle>> {

    /** Query URL */
    private String mUrl;

    public NewsLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<NewsArticle> loadInBackground() {
        if (mUrl == null) {
            return null;
        }
        // Perform the network request, parse the response, and extract a list of earthquakes.
        List<NewsArticle> earthquakes = QueryUtils.fetchNewsArticleData(mUrl);
        return earthquakes;
    }
}
