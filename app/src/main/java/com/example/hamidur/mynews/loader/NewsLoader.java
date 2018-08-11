package com.example.hamidur.mynews.loader;

import android.content.Context;
import android.content.AsyncTaskLoader;

import com.example.hamidur.mynews.utility.QueryUtils;
import com.example.hamidur.mynews.model.NewsArticle;

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
        return QueryUtils.fetchNewsArticleData(mUrl);
    }
}
