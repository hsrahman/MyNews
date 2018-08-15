package com.example.hamidur.mynews;

import android.Manifest;
import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hamidur.mynews.adapter.NewsAdapter;
import com.example.hamidur.mynews.loader.NewsLoader;
import com.example.hamidur.mynews.model.NewsArticle;
import com.example.hamidur.mynews.utility.QueryUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HeadlineActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<NewsArticle>>, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        ResultCallback<LocationSettingsResult> {

    private static final int DEFAULT_RESULT_AMOUNT = 8;
    private static final int DEFAULT_PAGE = 1;
    private static final String DEFAULT_COUNTRY_CODE = "us";
    private static final String DEFAULT_COUNTRY = "United States";

    private String defaultLang = DEFAULT_COUNTRY_CODE;
    private int resultAmount = DEFAULT_RESULT_AMOUNT;
    private int page = DEFAULT_PAGE;
    private String currentSelection = DEFAULT_COUNTRY;

    private NewsAdapter mAdapter;

    private static final int HEADLINE_LOADER_ID = 1;

    private static final String NEWSAPI_REQUEST_URL = "https://newsapi.org/v2/top-headlines";

    private ListView headlineListView;
    private TextView emptyStateTextView;
    private View loadingIndicator;

    private Menu actionMenu = null;

    protected static final int REQUEST_SETTINGS_APP = 0x2;

    protected static final int REQUEST_CHECK_SETTINGS = 0x1;

    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    protected final static String KEY_REQUESTING_LOCATION_UPDATES = "requesting-location-updates";
    protected final static String KEY_LOCATION = "location";
    protected final static String KEY_LAST_UPDATED_TIME_STRING = "last-updated-time-string";

    private ConnectivityManager connectivityManager;

    protected GoogleApiClient mGoogleApiClient;

    protected LocationRequest mLocationRequest;

    protected LocationSettingsRequest mLocationSettingsRequest;

    protected Location mCurrentLocation;

    protected Boolean mRequestingLocationUpdates;

    private Button moreBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_headline);

        headlineListView = (ListView) findViewById(R.id.headlineList);
        emptyStateTextView = (TextView) findViewById(R.id.empty_view);
        loadingIndicator = (View) findViewById(R.id.loading_indicator);
        moreBtn = new Button(HeadlineActivity.this);
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        mAdapter = new NewsAdapter(this, new ArrayList<NewsArticle>());
        headlineListView.setAdapter(mAdapter);
        headlineListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NewsArticle selectedArticle = mAdapter.getItem(position);
                Uri newsUri = Uri.parse(selectedArticle.getSourceUrl());
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, newsUri);
                startActivity(websiteIntent);
            }
        });
        headlineListView.setEmptyView(emptyStateTextView);
        NetworkInfo net = connectivityManager.getActiveNetworkInfo();
        if (net != null && net.isConnected()) {
            getLoaderManager().initLoader(HEADLINE_LOADER_ID, null, this);
        } else {
            loadingIndicator.setVisibility(View.GONE);
            emptyStateTextView.setText("Failed to retrieve data");
        }

        mRequestingLocationUpdates = false;
        buildGoogleApiClient();
        createLocationRequest();
        buildLocationSettingsRequest();
    }

    private boolean isNetworkConnected () {
        NetworkInfo net = connectivityManager.getActiveNetworkInfo();
        boolean connection = (net != null && net.isConnected());
        if (!connection) {
            if (mAdapter.isEmpty()) {
                loadingIndicator.setVisibility(View.GONE);
                emptyStateTextView.setText("Failed to retrieve data");
            } else {
                Toast.makeText(HeadlineActivity.this, "Failed to retrieve data", Toast.LENGTH_LONG).show();
            }
        }
        return connection;
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelable(KEY_LOCATION, mCurrentLocation);
        super.onSaveInstanceState(savedInstanceState);
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(HeadlineActivity.this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();

        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }

    protected void checkLocationSettings() {
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(
                        mGoogleApiClient,
                        mLocationSettingsRequest
                );
        result.setResultCallback(this);
        loadingIndicator.setVisibility(View.VISIBLE);
        toggleMenuButtons(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        actionMenu = menu;
        inflater.inflate(R.menu.menu, menu);
        toggleMenuButtons(false);
        if (isLocationEnabled()) checkLocationSettings();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.action_refresh:
                if (isNetworkConnected ()) {
                    mAdapter.clear(); // clear all old data
                    page = DEFAULT_PAGE; // reset to first page
                    resultAmount  = DEFAULT_RESULT_AMOUNT; // reset to correct result amount
                    loadingIndicator.setVisibility(View.VISIBLE);
                    getLoaderManager().restartLoader(HEADLINE_LOADER_ID, null, this);
                    toggleMenuButtons(false);
                }
                break;
            case R.id.action_lang:
                AlertDialog.Builder spinnerDialogue = new AlertDialog.Builder(HeadlineActivity.this);
                View v = getLayoutInflater().inflate(R.layout.country_spinner_dialogue, null);
                spinnerDialogue.setTitle("Select Country");
                final Spinner spinner = (Spinner) v.findViewById(R.id.country_spinner);

                final ArrayAdapter<String> adapter = new ArrayAdapter<String>(HeadlineActivity.this,
                        android.R.layout.simple_spinner_item,
                        getResources().getStringArray(R.array.countries));
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);
                spinner.setSelection(adapter.getPosition(currentSelection));
                spinnerDialogue.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (isNetworkConnected()){
                            String item = spinner.getSelectedItem().toString();
                            try {
                                JSONArray isoList = new JSONArray(QueryUtils.readFromRawJsonFile(getResources(),R.raw.iso_json));
                                for(int i = 0; i < isoList.length(); i++) {
                                    JSONObject currentCountry = isoList.getJSONObject(i);
                                    if (currentCountry.getString("Name").equals(item)) {
                                        resetQueryDetails ();
                                        spinner.setSelection(adapter.getPosition(item));
                                        currentSelection = item;
                                        defaultLang = currentCountry.getString("Code").toLowerCase();
                                        loadingIndicator.setVisibility(View.VISIBLE);
                                        getLoaderManager().restartLoader(HEADLINE_LOADER_ID, null, HeadlineActivity.this);
                                        toggleMenuButtons(false);
                                        break;
                                    }
                                }
                            }catch (Exception ex) {
                                System.out.println("Error reading ISO JSON File.");
                            }
                        }
                        dialog.dismiss();
                    }
                });

                spinnerDialogue.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                spinnerDialogue.setView(v);
                AlertDialog alertDialog = spinnerDialogue.create();
                alertDialog.show();

                break;
            case R.id.action_loc:
                if (!isLocationEnabled()) {
                    checkLocationSettings();
                } else {
                    startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), REQUEST_SETTINGS_APP);
                }
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
        builder.appendQueryParameter("pageSize", String.valueOf(resultAmount));
        builder.appendQueryParameter("page", String.valueOf(page));
        return new NewsLoader(this, builder.toString());

    }

    @Override
    public void onLoadFinished(Loader<List<NewsArticle>> loader, final List<NewsArticle> newsArticles) {
        loadingIndicator.setVisibility(View.GONE);

        if (newsArticles != null && !newsArticles.isEmpty()) {
            if (newsArticles.get(0).getTotal() > (resultAmount*page)) {

                moreBtn.setText("See More");
                // add button onclick feature
                moreBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isNetworkConnected()) {
                            toggleMenuButtons(false);
                            page++;
                            loadingIndicator.setVisibility(View.VISIBLE);
                            getLoaderManager().restartLoader(HEADLINE_LOADER_ID, null, HeadlineActivity.this);
                        }
                    }
                });
                if (headlineListView.getFooterViewsCount() < 1)
                    headlineListView.addFooterView(moreBtn);
            } else {
                headlineListView.removeFooterView(moreBtn);
            }
            mAdapter.addAll(newsArticles);
            mAdapter.notifyDataSetChanged();
        }
        toggleMenuButtons(true);
    }

    @Override
    public void onLoaderReset(Loader<List<NewsArticle>> loader) {
        mAdapter.clear();
    }

    private boolean isLocationEnabled () {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (gps_enabled) {
                actionMenu.getItem(0).setIcon(R.drawable.ic_location_on);
            } else {
                actionMenu.getItem(0).setIcon(R.drawable.ic_location_off);
            }
        } catch (Exception ex) {}
        return gps_enabled;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onResult(LocationSettingsResult locationSettingsResult) {
        final Status status = locationSettingsResult.getStatus();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
                startLocationUpdates();
                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                try {
                    status.startResolutionForResult(HeadlineActivity.this, REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException e) {}
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:

                break;
        }
    }

    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient,
                mLocationRequest,
                this
        ).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(Status status) {
                mRequestingLocationUpdates = true;
            }
        });

    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient,
                this
        ).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(Status status) {
                mRequestingLocationUpdates = false;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        toggleMenuButtons(false);
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        startLocationUpdates();
                        break;
                    case Activity.RESULT_CANCELED:
                        toggleMenuButtons(true);
                        loadingIndicator.setVisibility(View.GONE);
                        break;
                }
                break;

            case REQUEST_SETTINGS_APP :
                if (!isLocationEnabled ()) {
                    if (isNetworkConnected()) {
                        resetQueryDetails ();
                        getLoaderManager().restartLoader(HEADLINE_LOADER_ID, null, HeadlineActivity.this);
                    } else {
                        toggleMenuButtons(true);
                    }
                }

                break;
        }
    }

    private void resetQueryDetails () {
        mAdapter.clear(); // clear all old data
        page = DEFAULT_PAGE; // reset to first page
        resultAmount = DEFAULT_RESULT_AMOUNT; // reset to correct result amount
        defaultLang = DEFAULT_COUNTRY_CODE;
        currentSelection =  DEFAULT_COUNTRY;
    }


    private void toggleMenuButtons (boolean enable) {
        actionMenu.getItem(0).setEnabled(enable);
        actionMenu.getItem(1).setEnabled(enable);
        actionMenu.getItem(2).setEnabled(enable);
    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
       if (location != null) {
           Geocoder coder = new Geocoder(getApplicationContext(), Locale.getDefault());
           try {
               List<Address> addresses = coder.getFromLocation(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude(), 1);
               if (addresses.size() > 0) {
                   resetQueryDetails ();
                   defaultLang = addresses.get(0).getCountryCode().toLowerCase();
                   currentSelection = addresses.get(0).getCountryName();
                   if (isNetworkConnected()) {
                       getLoaderManager().restartLoader(HEADLINE_LOADER_ID, null, HeadlineActivity.this);
                   } else {
                       toggleMenuButtons(true);
                       loadingIndicator.setVisibility(View.GONE);
                   }
                   isLocationEnabled();
                   stopLocationUpdates();
               } else {
                   System.out.println("No locations found...");
               }
           } catch (IOException e) {
               e.printStackTrace();
           }
       } else {
           System.out.println("NULL LOCATION");
       }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
        //getLoaderManager().destroyLoader(HEADLINE_LOADER_ID);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            stopLocationUpdates();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }
}
