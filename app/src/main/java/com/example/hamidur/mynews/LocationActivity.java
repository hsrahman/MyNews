package com.example.hamidur.mynews;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hamidur.mynews.model.Location;
import com.example.hamidur.mynews.loader.LocationLoader;
import com.google.gson.Gson;

import java.util.List;

public class LocationActivity extends AppCompatActivity  implements LoaderManager.LoaderCallbacks<List<Location>>{

    private static final String GEONAMESAPI_REQUEST_URL_SOURCE = " http://api.geonames.org/searchJSON";
    private static final int LOCATION_LOADER_ID = 4;

    private LocationAdapter mAdapter;
    ListView list;

    private String search = "Lodon";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        TextView searchBtn = (TextView) findViewById(R.id.searchLoc);
        final TextView searchText = (TextView) findViewById(R.id.search_bar);

        list = (ListView) findViewById(R.id.list) ;

        searchText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                return false;
            }
        });

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("my_sources", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                Gson gson = new Gson();
                Location l = mAdapter.getItem(position);
                editor.remove(getResources().getString(R.string.location_pref));
                editor.commit();
                editor.putString(getResources().getString(R.string.location_pref), gson.toJson(l));
                editor.commit();
                Toast.makeText(LocationActivity.this, "Your location is " + l.getAsciiName() , Toast.LENGTH_LONG).show();
            }
        });

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search = searchText.getText().toString();
                if (!search.trim().equals("")) {
                    ConnectivityManager connMgr = (ConnectivityManager)
                            getSystemService(Context.CONNECTIVITY_SERVICE);

                    NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

                    if (networkInfo != null && networkInfo.isConnected()) {
                        LoaderManager loaderManager = getLoaderManager();

                        loaderManager.initLoader(LOCATION_LOADER_ID, null, LocationActivity.this);
                    }
                }
            }
        });
    }


    @Override
    public Loader<List<Location>> onCreateLoader(int id, Bundle args) {
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.VISIBLE);

        Uri baseUri = Uri.parse(GEONAMESAPI_REQUEST_URL_SOURCE);
        Uri.Builder uriBuilder = baseUri.buildUpon();
        uriBuilder.appendQueryParameter("formatted", "true");
        uriBuilder.appendQueryParameter("q", search.toLowerCase());
        uriBuilder.appendQueryParameter("maxRows", "10");
        uriBuilder.appendQueryParameter("lang", "en");
        uriBuilder.appendQueryParameter("username", "hsrahman");
        uriBuilder.appendQueryParameter("style", "full");

        return new LocationLoader(this,uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<Location>> loader, List<Location> data) {
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);

        TextView emptyStateTextView = (TextView) findViewById(R.id.empty_view);



        mAdapter = new LocationAdapter(this, data);

        if (data != null && !data.isEmpty()) {
            mAdapter.addAll(data);
        } else {
            emptyStateTextView.setText(R.string.no_location);
        }

        list.setAdapter(mAdapter);

        getLoaderManager().destroyLoader(LOCATION_LOADER_ID);
    }

    @Override
    public void onLoaderReset(Loader<List<Location>> loader) {
        mAdapter.clear();
    }

    private class LocationAdapter extends ArrayAdapter<Location> {

        public LocationAdapter(Context context, List<Location> locations) {
            super(context, 0, locations);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View listItemView = convertView;
            if (listItemView == null) {
                listItemView = LayoutInflater.from(getContext()).inflate(
                        R.layout.locations_item, parent, false);
            }

            Location location = getItem(position);

            TextView locationName = (TextView) listItemView.findViewById(R.id.location_ascii_name);
            locationName.setText(location.getAsciiName());

            TextView timeZone = (TextView) listItemView.findViewById(R.id.location_timezone);
            timeZone.setText(location.getTimeZoneId());

            TextView countryCode = (TextView) listItemView.findViewById(R.id.location_country_code);
            countryCode.setText(location.getCountryCode());

            GradientDrawable circle = (GradientDrawable) countryCode.getBackground();
            circle.setColor(getContinentColour(location.getContinentCode()));

            TextView latLong = (TextView) listItemView.findViewById(R.id.location_lat_lng);
            latLong.setText("Lat: " + location.getLat() + ", Lng: " + location.getLng());

            return listItemView;
        }

        private int getContinentColour(String code){
            int locationColorResourceId;

            switch(code){
                case "EU":
                    locationColorResourceId = R.color.europe;
                    break;
                case "NA":
                    locationColorResourceId = R.color.north_america;
                    break;
                case "SA":
                    locationColorResourceId = R.color.south_america;
                    break;
                case "AS":
                    locationColorResourceId = R.color.asia;
                    break;
                case "AF":
                    locationColorResourceId = R.color.africa;
                    break;
                case "OC":
                    locationColorResourceId = R.color.oceania;
                    break;
                default:
                    locationColorResourceId = R.color.antarctica;
            }
            return ContextCompat.getColor(getContext(), locationColorResourceId);
        }
    }
}
