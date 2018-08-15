package com.example.hamidur.mynews.fragment;


import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.hamidur.mynews.R;
import com.example.hamidur.mynews.adapter.NewsAdapter;
import com.example.hamidur.mynews.loader.NewsLoader;
import com.example.hamidur.mynews.model.NewsArticle;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<NewsArticle>>{

    private String searchString = "";

    private static final int SEARCH_LOADER_ID = 10;
    private NewsAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;

    private View loadingIndicator;
    private static final String NEWSAPI_REQUEST_URL = "https://newsapi.org/v2/everything";
    public SearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_search, container, false);

        ListView mListView = (ListView) rootView.findViewById(R.id.search_result);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NewsArticle selectedArticle = mAdapter.getItem(position);
                Uri newsUri = Uri.parse(selectedArticle.getSourceUrl());
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, newsUri);
                startActivity(websiteIntent);
            }
        });

        loadingIndicator = (View) rootView.findViewById(R.id.loading_indicator);
        mAdapter = new NewsAdapter(getActivity(), new ArrayList<NewsArticle>());
        mLayoutManager = new LinearLayoutManager(getActivity());
        mListView.setAdapter(mAdapter);
        setHasOptionsMenu(true);
        return rootView;
    }

    private void initaliseLoader(){

        ConnectivityManager connMgr = (ConnectivityManager)
                getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            LoaderManager loaderManager = getActivity().getLoaderManager();

            loaderManager.restartLoader(SEARCH_LOADER_ID, null, this);
        } else {
            loadingIndicator.setVisibility(View.GONE);
            Toast.makeText(getActivity(), "Your device needs internet access for this operation", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getActivity().getSystemService(getContext().SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getActivity().getComponentName()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                loadingIndicator.setVisibility(View.VISIBLE);
                performSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

    }

    private void performSearch(String query){
        searchString = query;
        initaliseLoader();
    }


    @Override
    public Loader<List<NewsArticle>> onCreateLoader(int id, Bundle args) {
        Uri baseUri = Uri.parse(NEWSAPI_REQUEST_URL);
        Uri.Builder builder = baseUri.buildUpon();
        builder.appendQueryParameter("q", searchString);
        builder.appendQueryParameter("sortBy", "relevancy");
        return new NewsLoader(getActivity(), builder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<NewsArticle>> loader, List<NewsArticle> newsArticles) {
        loadingIndicator.setVisibility(View.GONE);
        mAdapter.clear();
        if (newsArticles != null && !newsArticles.isEmpty()) {
            mAdapter.addAll(newsArticles);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<NewsArticle>> loader) {
        mAdapter.clear();
    }
}
