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
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hamidur.mynews.adapter.SourceAdapter;
import com.example.hamidur.mynews.loader.SourceLoader;
import com.example.hamidur.mynews.model.Source;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class SourceActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, LoaderManager.LoaderCallbacks<List<Source>> {

    private static final String NEWSAPI_REQUEST_URL_SOURCE = " https://newsapi.org/v2/sources";

    private HashMap <String, List<Source>> categoryToSource;

    private String selectedCategory = "science";

    private static final int SOURCE_LOADER_ID = 2;

    private SourceAdapter mAdapter;

    private static final int MAX_SELECTABLE = 3;

    TextView emptyView;
    View loadingIndicator;
    Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_source);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        ListView sourceListView = (ListView) findViewById(R.id.sourcelist);
        // instantiate hasmap
        categoryToSource = new HashMap<>();

        mAdapter = new SourceAdapter(this, new ArrayList<Source>());
        sourceListView.setAdapter(mAdapter);
        emptyView = (TextView) findViewById(R.id.empty_view);
        loadingIndicator = findViewById(R.id.source_loading_indicator);
        spinner = (Spinner) findViewById(R.id.categories);
        sourceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Gson gson = new Gson();
                Set<String> prefs = getApplicationContext().getSharedPreferences("my_sources", Context.MODE_PRIVATE).getStringSet(getResources().getString(R.string.source_pref), new ArraySet<String>());
                SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("my_sources", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                Source currentSource =  mAdapter.getItem(position);
                if(!currentSource.isSelected()) {
                    if (prefs.size() != MAX_SELECTABLE) {
                        currentSource.setSelected(true);
                        prefs.add(gson.toJson(currentSource));
                        editor.clear();
                        editor.putStringSet(getResources().getString(R.string.source_pref), prefs);
                        editor.commit();
                        view.setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.selected_source));
                    } else {
                        Toast.makeText(getApplicationContext(), "You cannot select more then 3 sources", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    for (Iterator<String> iterator = prefs.iterator(); iterator.hasNext();){
                        Source s = gson.fromJson(iterator.next(), Source.class);
                        if (s.getId().equals(mAdapter.getItem(position).getId())) {
                            iterator.remove();
                        }
                    }
                    currentSource.setSelected(false);
                    editor.remove(getResources().getString(R.string.source_pref));
                    editor.commit();
                    editor.putStringSet(getResources().getString(R.string.source_pref), prefs); // may need to be done manually
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
        } else {
            loadingIndicator.setVisibility(View.GONE);
            emptyView.setText(getResources().getString(R.string.no_internet_connection));
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    @Override
    public void onLoadFinished(Loader<List<Source>> loader, List<Source> sources) {
        loadingIndicator.setVisibility(View.GONE);

        for(int i = 0; i < sources.size(); i++){
            Source s = sources.get(i);
            // check newly created sources has been selected therefor it has to be set as selected
            if(getPrefInSet(s.getId()) != null){
                s.setSelected(true);
            }
            // store in hashmap
            if(categoryToSource.get(s.getCategory()) == null){ // if list doesn't exist yet
                categoryToSource.put(s.getCategory(), new ArrayList<Source>());
            }

            if(!existsInCollection(s, s.getId())){
                categoryToSource.get(s.getCategory()).add(s);
            }

        }

        SpinnerAdapter spinnerAdapter = new SpinnerAdapter(this, new ArrayList<>(categoryToSource.keySet()));
        spinner.setVisibility(View.VISIBLE);
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(this);

        mAdapter.clear();

        if (sources != null && !sources.isEmpty()) {
           // mAdapter.addAll(categoryToSource.get(selectedCategory));
        }
    }

    private boolean existsInCollection(Source itemInCollection, String itemToBeChecked){
        for(int j = 0; j < categoryToSource.get(itemInCollection.getCategory()).size(); j++){
            if(categoryToSource.get(itemInCollection.getCategory()).get(j).getId().equals(itemToBeChecked)){
                return true;
            }
        }
        return false;
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
        Set<String> prefs = getApplicationContext().getSharedPreferences("my_sources", Context.MODE_PRIVATE).getStringSet(getResources().getString(R.string.source_pref), new ArraySet<String>());
        Gson gson = new Gson();
        for(String pref : prefs) {
            Source source = gson.fromJson(pref, Source.class);
            if(source.getId().equals(id)){
                return pref;
            }
        }
        return null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home :
                finish();
                break;

            default:
                break;
        }

        return true;
    }

    private class SpinnerAdapter extends ArrayAdapter<String> {


        public SpinnerAdapter(Context context, List<String> sources) {
            super(context,0, sources);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            return setUpLayoutView(position, convertView, parent);
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {

            return setUpLayoutView(position, convertView, parent);
        }

        private View setUpLayoutView(int position, View convertView, ViewGroup parent){
            View listItemView = convertView;
            if (listItemView == null) {
                listItemView = LayoutInflater.from(getContext()).inflate(
                        R.layout.spinner_item, parent, false);
            }

            String s = getItem(position);

            ImageView sourceIcon = (ImageView) listItemView.findViewById(R.id.source_icon);
            sourceIcon.setImageResource(setIconId(s));
            TextView category = (TextView) listItemView.findViewById(R.id.category);
            category.setText(s);

            return listItemView;
        }

        private int setIconId (String category) {
            switch (category) {
                case "science":
                    return R.drawable.ic_nature;
                case "health":
                    return R.drawable.ic_health;
                case "technology":
                    return R.drawable.ic_tech;
                case "sports":
                    return R.drawable.ic_sport;
                case "entertainment":
                    return R.drawable.ic_entertainment;
                case "business":
                    return R.drawable.ic_business;
                default:
                    return R.drawable.ic_general;
            }
        }
    }

}