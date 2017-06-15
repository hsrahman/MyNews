package com.example.hamidur.mynews;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

public class SourceActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, LoaderManager.LoaderCallbacks<List<Source>> {

    private static final String NEWSAPI_REQUEST_URL_SOURCE = " https://newsapi.org/v1/sources";

    private String selectedCategory = "sport";

    private static final int SOURCE_LOADER_ID = 2;

    private SourceAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_source);
        ListView sourceListView = (ListView) findViewById(R.id.sourcelist);

        ArrayList<String> categories =  new ArrayList<>();
        categories.add("sport");
        categories.add("general");
        categories.add("gaming");

        mAdapter = new SourceAdapter(this, new ArrayList<Source>());
        sourceListView.setAdapter(mAdapter);
        Spinner spinner = (Spinner) findViewById(R.id.categories);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
              android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        makeOnlineApiCall();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        selectedCategory = parent.getItemAtPosition(position).toString();

        makeOnlineApiCall();
    }

    private void makeOnlineApiCall(){
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if(networkInfo != null && networkInfo.isConnected()){
            LoaderManager loaderManager = getLoaderManager();

            loaderManager.initLoader(SOURCE_LOADER_ID, null, this);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onLoadFinished(Loader<List<Source>> loader, List<Source> sources) {
        View loadingIndicator = findViewById(R.id.source_loading_indicator);
        loadingIndicator.setVisibility(View.GONE);

        mAdapter.clear();

        if (sources != null && !sources.isEmpty()) {
            mAdapter.addAll(sources);
        }
    }

    @Override
    public Loader<List<Source>> onCreateLoader(int i, Bundle bundle){
        Uri baseUri = Uri.parse(NEWSAPI_REQUEST_URL_SOURCE);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter("category", selectedCategory);;

        return new SourceLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoaderReset(Loader<List<Source>> loader){
        mAdapter.clear();
    }
}