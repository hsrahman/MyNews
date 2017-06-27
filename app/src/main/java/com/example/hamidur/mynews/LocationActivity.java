package com.example.hamidur.mynews;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class LocationActivity extends AppCompatActivity  implements LoaderManager.LoaderCallbacks<List<Location>>{

    private static final String GEONAMESAPI_REQUEST_URL_SOURCE = " http://api.geonames.org/searchJSON";
    private static final int LOCATION_LOADER_ID = 4;

    private LocationAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
    }


    @Override
    public Loader<List<Location>> onCreateLoader(int id, Bundle args) {
        Uri baseUri = Uri.parse(GEONAMESAPI_REQUEST_URL_SOURCE);
        Uri.Builder uriBuilder = baseUri.buildUpon();
        uriBuilder.appendQueryParameter("formatted", "true");
        uriBuilder.appendQueryParameter("q", "london");
        uriBuilder.appendQueryParameter("maxRows", "10");
        uriBuilder.appendQueryParameter("lang", "en");
        uriBuilder.appendQueryParameter("username", "hsrahman");
        uriBuilder.appendQueryParameter("style", "full");
        return new LocationLoader(this,uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<Location>> loader, List<Location> data) {

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
                        R.layout.location_item, parent, false);
            }

            Location location = getItem(position);

            TextView locationName = (TextView) listItemView.findViewById(R.id.location_ascii_name);
            locationName.setText(location.getAsciiName());

            TextView timeZone = (TextView) listItemView.findViewById(R.id.location_timezone);
            timeZone.setText(location.getTimeZoneId());

            TextView countryCode = (TextView) listItemView.findViewById(R.id.location_country_code);
            countryCode.setText(location.getCountryCode());

            TextView latLong = (TextView) listItemView.findViewById(R.id.location_lat_lng);
            latLong.setText("Lat: " + location.getLat() + ", Lng: " + location.getLng());

            return listItemView;
        }
    }
}
