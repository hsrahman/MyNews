package com.example.hamidur.mynews;


import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.ArraySet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 */
public class OptionOneFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<NewsArticle>> {

    private NewsAdapter mAdapter;

    private static final int ARTICLE_LOADER_ID = 1;

    private static final String NEWSAPI_REQUEST_URL = "https://newsapi.org/v1/articles";

    private TextView emptyStateTextView;

    public OptionOneFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.news_list, container, false);

        ListView newsListView = (ListView) rootView.findViewById(R.id.list);

        mAdapter = new NewsAdapter(getActivity(), new ArrayList<NewsArticle>());

        emptyStateTextView = (TextView) rootView.findViewById(R.id.empty_view);

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
                getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            LoaderManager loaderManager = getActivity().getLoaderManager();
            loaderManager.initLoader(ARTICLE_LOADER_ID, null, this);
        } else {
            View loadingIndicator = rootView.findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);
            emptyStateTextView.setText(R.string.no_internet_connection);
        }
        return rootView;
    }


    @Override
    public void onLoadFinished(Loader<List<NewsArticle>> loader, List<NewsArticle> newsArticles) {
        View loadingIndicator = getActivity().findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);

        System.out.println("onLoadFinished freg 1");

        emptyStateTextView.setText(R.string.no_news);

        mAdapter.clear();

        if (newsArticles != null && !newsArticles.isEmpty()) {
            mAdapter.addAll(newsArticles);
        }
    }


    @Override
    public Loader<List<NewsArticle>> onCreateLoader(int i, Bundle bundle) {
        Set<String> ids = getActivity().getSharedPreferences("my_sources", Context.MODE_PRIVATE).getStringSet("source", new ArraySet<String>());
        Uri baseUri = Uri.parse(NEWSAPI_REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();
        int counter = 0;
        for (String id : ids) {
            counter++;
            if (counter == 1) {
                uriBuilder.appendQueryParameter("source", id);
                //uriBuilder.appendQueryParameter("sortBy", "latest");
            }
        }
        return new NewsLoader(getActivity(), uriBuilder.toString());
    }

    @Override
    public void onLoaderReset(Loader<List<NewsArticle>> loader) {
        mAdapter.clear();
    }

}
