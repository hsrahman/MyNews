package com.example.hamidur.mynews.fragment;


import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.util.ArraySet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hamidur.mynews.adapter.NewsAdapter;
import com.example.hamidur.mynews.R;
import com.example.hamidur.mynews.loader.NewsLoader;
import com.example.hamidur.mynews.model.NewsArticle;
import com.example.hamidur.mynews.model.Source;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 */
public class OptionOneFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<NewsArticle>> {

    private NewsAdapter mAdapter;

    private static final int ARTICLE_LOADER_ID = 1;

    private static final String NEWSAPI_REQUEST_URL = "https://newsapi.org/v2/top-headlines";

    private TextView emptyStateTextView;
    private ListView newsListView;
    private View rootView;

    private int resultAmount = 8;
    private int currentPage = 1;
    private int prevPage = 0;

    private boolean flag_loading = false;

    private ConnectivityManager connMgr;

    Toast lastToast = null; // Class member variable

    public OptionOneFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.news_list, container, false);

        newsListView = (ListView) rootView.findViewById(R.id.list);

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

        connMgr = (ConnectivityManager)
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

    private void loadMoreData () {
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            currentPage++;
            getActivity().getLoaderManager().restartLoader(ARTICLE_LOADER_ID, null, this);
        } else {
            if (lastToast != null) {
                try {
                    lastToast.getView().isShown(); // true if visible
                } catch (Exception e) {
                    createToast ();
                }
            } else {
                createToast ();
            }
        }

    }

    private void createToast () {
        lastToast = Toast.makeText(getActivity(), "Failed to load more data." , Toast.LENGTH_SHORT);
        lastToast.show();
    }

    @Override
    public void onLoadFinished(Loader<List<NewsArticle>> loader, final List<NewsArticle> newsArticles) {
        View loadingIndicator = rootView.findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);
        if (newsArticles != null && !newsArticles.isEmpty()) {
            newsListView.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {

                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    if(firstVisibleItem+visibleItemCount == totalItemCount && totalItemCount!=0)
                    {
                        if(!flag_loading)
                        {
                            flag_loading = true;

                            if (newsArticles.get(0).getTotal() > (resultAmount*currentPage)) {
                                flag_loading = false;
                                loadMoreData ();
                            }
                        }
                    }
                }
            });

            if (prevPage != currentPage) {
                mAdapter.addAll(newsArticles);
                mAdapter.notifyDataSetChanged();
            }

            prevPage = currentPage;

        } else {
            emptyStateTextView.setText(R.string.no_news);
        }

    }



    @Override
    public Loader<List<NewsArticle>> onCreateLoader(int i, Bundle bundle) {
        Set<String> sources = getActivity().getSharedPreferences("my_sources", Context.MODE_PRIVATE).getStringSet(getResources().getString(R.string.source_pref), new ArraySet<String>());
        Uri baseUri = Uri.parse(NEWSAPI_REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();
        int counter = 0;
        for (String source : sources) {
            counter++;
            if (counter == 1) {
                Gson gson = new Gson();
                Source obj = gson.fromJson(source, Source.class);
                uriBuilder.appendQueryParameter("sources", obj.getId());
                uriBuilder.appendQueryParameter("pageSize", String.valueOf(resultAmount));
                uriBuilder.appendQueryParameter("page", String.valueOf(currentPage));
            }
        }

        return new NewsLoader(getActivity(), uriBuilder.toString());
    }

    @Override
    public void onLoaderReset(Loader<List<NewsArticle>> loader) {
        mAdapter.clear();
    }
}
