package com.example.hamidur.mynews;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hamidur.mynews.adapter.NewsAdapter;
import com.example.hamidur.mynews.loader.NewsLoader;
import com.example.hamidur.mynews.model.NewsArticle;

import java.util.ArrayList;
import java.util.List;

public class HeadlineActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<NewsArticle>>{

    private String defaultLang = "us";
    private String resultAmount = "25";

    private boolean gps_enabled = false;

    private NewsAdapter mAdapter;

    private static final int HEADLINE_LOADER_ID = 1;

    private static final String NEWSAPI_REQUEST_URL = "https://newsapi.org/v2/top-headlines";

    private ListView headlineListView;
    private TextView emptyStateTextView;
    private View loadingIndicator;

    private Menu actionMenu = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_headline);

        headlineListView = (ListView) findViewById(R.id.headlineList);
        emptyStateTextView = (TextView) findViewById(R.id.empty_view);
        loadingIndicator = (View) findViewById(R.id.loading_indicator);

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        mAdapter = new NewsAdapter(this, new ArrayList<NewsArticle>());
        headlineListView.setAdapter(mAdapter);
        headlineListView.setEmptyView(emptyStateTextView);
        NetworkInfo net = connectivityManager.getActiveNetworkInfo();
        if (net != null && net.isConnected()) {
            getLoaderManager().initLoader(HEADLINE_LOADER_ID, null, this);
        } else {
            loadingIndicator.setVisibility(View.GONE);
            emptyStateTextView.setText("Failed to retrieve data");
        }

        getSupportActionBar().setTitle("Headline");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        actionMenu = menu;
        inflater.inflate(R.menu.menu, menu);
        isLocationEnabled();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.action_refresh:
                loadingIndicator.setVisibility(View.VISIBLE);
                getLoaderManager().restartLoader(HEADLINE_LOADER_ID, null, this);
                break;
            case R.id.action_lang:
                Toast.makeText(this, "Language settings selected", Toast.LENGTH_SHORT)
                        .show();
                break;
            case R.id.action_loc:
               if (gps_enabled) {
                   Toast.makeText(this, "Location On", Toast.LENGTH_SHORT)
                           .show();
               } else {
                   Toast.makeText(this, "Location Off", Toast.LENGTH_SHORT)
                           .show();
               }
                break;
            default:
                break;
        }

        return true;
    }

    @Override
    public Loader<List<NewsArticle>> onCreateLoader(int id, Bundle args) {
        Uri baseUri = Uri.parse(NEWSAPI_REQUEST_URL);
        Uri.Builder builder = baseUri.buildUpon();
        builder.appendQueryParameter("country", defaultLang);
        builder.appendQueryParameter("pageSize", resultAmount);
        return new NewsLoader(this, builder.toString());
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

    private void isLocationEnabled () {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (gps_enabled) {
                actionMenu.getItem(0).setIcon(R.drawable.ic_location_on);
            } else {
                actionMenu.getItem(0).setIcon(R.drawable.ic_location_off);
            }
        } catch (Exception ex) {}
    }
}
