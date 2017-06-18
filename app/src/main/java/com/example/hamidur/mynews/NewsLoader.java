package com.example.hamidur.mynews;

import android.content.Context;
import android.content.AsyncTaskLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pro-developer Hamidur on 13/06/2017.
 */
public class NewsLoader extends AsyncTaskLoader<List<List<NewsArticle>>> {

    /** Query URL */
    private List<String> mUrl;

    public NewsLoader(Context context, List<String> url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<List<NewsArticle>> loadInBackground() {
        List<List<NewsArticle>> allNews = new ArrayList<>();
        if (mUrl == null) {
            return null;
        }
        for (int i = 0; i < mUrl.size(); i++) {
            // Perform the network request, parse the response, and extract a list of earthquakes.
            List<NewsArticle> newsArticles = QueryUtils.fetchNewsArticleData(mUrl.get(i));
            allNews.add(newsArticles);
        }
        return allNews;
    }
}
