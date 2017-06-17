package com.example.hamidur.mynews;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.ArraySet;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class SourceActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, LoaderManager.LoaderCallbacks<List<Source>> {

    private static final String NEWSAPI_REQUEST_URL_SOURCE = " https://newsapi.org/v1/sources";

    private HashMap <String, List<Source>> categoryToSource;

    private String selectedCategory = "sport";

    private static final int SOURCE_LOADER_ID = 2;

    private SourceAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_source);
        ListView sourceListView = (ListView) findViewById(R.id.sourcelist);
        // instantiate hasmap
        categoryToSource = new HashMap<>();

        mAdapter = new SourceAdapter(this, new ArrayList<Source>());
        sourceListView.setAdapter(mAdapter);

        sourceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Set<String> prefs = getApplicationContext().getSharedPreferences("my_sources", Context.MODE_PRIVATE).getStringSet("source", new ArraySet<String>());
                SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("my_sources", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                Source currentSource =  mAdapter.getItem(position);
                if(!currentSource.isSelected()) {
                    currentSource.setSelected(true);
                    prefs.add(currentSource.getId());
                    editor.putStringSet("source", prefs);
                    editor.commit();
                    view.setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.selected_source));
                } else {
                    currentSource.setSelected(false);
                    prefs.remove(mAdapter.getItem(position).getId());
                    editor.putStringSet("source", prefs); // may need to be done manually
                    editor.commit();
                    view.setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.unselected_source));
                }
            }
        });

        makeOnlineApiCall();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        selectedCategory = parent.getItemAtPosition(position).toString();
        //getLoaderManager().restartLoader(SOURCE_LOADER_ID, null, this);
        //makeOnlineApiCall();
        mAdapter.clear();
        mAdapter.addAll(categoryToSource.get(selectedCategory));
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
        for(int i = 0; i < sources.size(); i++){
            Source s = sources.get(i);
            // check newly created sources has been selected therefor it has to be set as selected
            if(getPrefInSet(s.getId()) != null){
                System.out.println(getPreferences(Context.MODE_PRIVATE).getString("source", getString(R.string.my_source)));
                s.setSelected(true);
            }
            // store in hashmap
            if(categoryToSource.get(s.getCategory()) == null){ // if list doesn't exist yet
                categoryToSource.put(s.getCategory(), new ArrayList<Source>());
            }

            categoryToSource.get(s.getCategory()).add(s);
        }

        Spinner spinner = (Spinner) findViewById(R.id.categories);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, new ArrayList<String>(categoryToSource.keySet()));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        mAdapter.clear();

        if (sources != null && !sources.isEmpty()) {
            mAdapter.addAll(categoryToSource.get(selectedCategory));
        }
    }

    @Override
    public Loader<List<Source>> onCreateLoader(int i, Bundle bundle){
        Uri baseUri = Uri.parse(NEWSAPI_REQUEST_URL_SOURCE);
        return new SourceLoader(this, baseUri.toString());
    }

    @Override
    public void onLoaderReset(Loader<List<Source>> loader){
        mAdapter.clear();
    }

    private String getPrefInSet(String id){
        Set<String> prefs = getApplicationContext().getSharedPreferences("my_sources", Context.MODE_PRIVATE).getStringSet("source", new ArraySet<String>());
        for(String pref : prefs){
            if(pref.equals(id)){
                return pref;
            }
        }
        return null;
    }
}