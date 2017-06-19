package com.example.hamidur.mynews;


import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.util.ArraySet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 */
public class OptionOneFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<List<NewsArticle>>> {

    private NewsAdapter mAdapter;
    private static final String API_KEY = "b5b4806ba6834681baecc6492d59d788";

    private static final int ARTICLE_LOADER_ID = 1;

    private static final String NEWSAPI_REQUEST_URL = "https://newsapi.org/v1/articles";

    private TextView emptyStateTextView;

    public OptionOneFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.news_list, container, false);

        /* News Activity OnCreate

             ListView newsListView = (ListView) findViewById(R.id.list);

        mAdapter = new NewsAdapter(this, new ArrayList<NewsArticle>());

        emptyStateTextView = (TextView) findViewById(R.id.empty_view);
        newsListView.setEmptyView(emptyStateTextView);

        newsListView.setAdapter(mAdapter);

        newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NewsArticle selectedArticle = mAdapter.getItem(position);
                Uri newsUri = Uri.parse(selectedArticle.getSourceUrl());
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, newsUri);
                startActivity(websiteIntent);
            }
        });

        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if(networkInfo != null && networkInfo.isConnected()){
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(ARTICLE_LOADER_ID, null, this);
        } else {
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);
            emptyStateTextView.setText(R.string.no_internet_connection);
        }

         */


        return rootView;
    }

    @Override
    public void onLoadFinished(Loader<List<List<NewsArticle>>> loader, List<List<NewsArticle>> newsArticles) {
        View loadingIndicator = getActivity().findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);

        emptyStateTextView.setText(R.string.no_news);

        mAdapter.clear();

        if (newsArticles != null && !newsArticles.isEmpty()) {
            List<NewsArticle> allArticles = new ArrayList<>();
            for (List<NewsArticle> articles : newsArticles) {
                if (articles != null) allArticles.addAll(articles);
            }
            // randomis the news articles

            mAdapter.addAll(allArticles);
        }

        getLoaderManager().destroyLoader(ARTICLE_LOADER_ID);
    }

    @Override
    public Loader<List<List<NewsArticle>>> onCreateLoader(int i, Bundle bundle){
        List<String> allUrls = new ArrayList<>();
        Set<String> ids = getActivity().getSharedPreferences("my_sources", Context.MODE_PRIVATE).getStringSet("source", new ArraySet<String>());
        for (String id : ids) {
            Uri baseUri = Uri.parse(NEWSAPI_REQUEST_URL);
            Uri.Builder uriBuilder = baseUri.buildUpon();
            uriBuilder.appendQueryParameter("source", id);
            //uriBuilder.appendQueryParameter("sortBy", "latest");
            uriBuilder.appendQueryParameter("apiKey", API_KEY);
            allUrls.add(uriBuilder.toString());
        }
        return new NewsLoader(getActivity(), allUrls);
    }

    @Override
    public void onLoaderReset(Loader<List<List<NewsArticle>>> loader){
        mAdapter.clear();
    }

}
