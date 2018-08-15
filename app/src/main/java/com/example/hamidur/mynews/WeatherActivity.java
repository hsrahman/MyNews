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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import com.bumptech.glide.Glide;
import com.example.hamidur.mynews.loader.WeatherLoader;
import com.example.hamidur.mynews.model.Weather;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class WeatherActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Weather>>, ConnectionCallbacks,
        OnConnectionFailedListener,
        LocationListener,
        ResultCallback<LocationSettingsResult> {

    private static final String IMG_URL = "http://www.weatherunlocked.com/Images/icons/1/";

    private static final int REQUEST_CODE = 1;

    private static final int WEATHER_LOADER_ID = 3;

    private WeatherAdapter forcastAdapter;


    protected static final int REQUEST_CHECK_SETTINGS = 0x1;

    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    protected final static String KEY_LOCATION = "LOCATION";

    protected GoogleApiClient mGoogleApiClient;

    protected LocationRequest mLocationRequest;

    protected LocationSettingsRequest mLocationSettingsRequest;

    protected Location mCurrentLocation;

    protected Boolean mRequestingLocationUpdates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        ImageView settings = (ImageView) findViewById(R.id.weather_settings);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WeatherActivity.this, WeatherSettingsActivity.class));
            }
        });

        final ImageView refreshLocation = (ImageView) findViewById(R.id.refresh_loc);
        refreshLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isLocationUsed = getApplicationContext().getSharedPreferences("my_sources", Context.MODE_PRIVATE).getBoolean(getResources().getString(R.string.location_switch_pref), true);
                if (isLocationUsed && isLocationEnabled()) {
                    refreshLocation.setEnabled(false);
                    startLocationUpdates();
                    refreshLocation.setEnabled(true);
                } else {
                    Toast.makeText(getApplicationContext(), "You must enable location for this feature" ,Toast.LENGTH_LONG).show();
                }

            }
        });

        updateValuesFromBundle(savedInstanceState);
        mRequestingLocationUpdates = false;

        buildGoogleApiClient();
        createLocationRequest();
        buildLocationSettingsRequest();

    }

    private void updateValuesFromBundle(Bundle savedInstanceState){
        if (savedInstanceState != null) {
            if (savedInstanceState.keySet().contains(KEY_LOCATION)) {
                mCurrentLocation = savedInstanceState.getParcelable(KEY_LOCATION);
                getWeatherData();
            }
        }
    }

    private boolean isLocationEnabled(){
        int locationMode = 0;
        try {
            locationMode = Settings.Secure.getInt(this.getContentResolver(), Settings.Secure.LOCATION_MODE);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }

        return locationMode != Settings.Secure.LOCATION_MODE_OFF;
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
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

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(
                        mGoogleApiClient,
                        mLocationSettingsRequest
                );
        result.setResultCallback(this);
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelable(KEY_LOCATION, mCurrentLocation);
        super.onSaveInstanceState(savedInstanceState);
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
                    status.startResolutionForResult(WeatherActivity.this, REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException e) {}
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                break;
        }
    }

    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE :
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkLocationSettings();
                } else {
                    finish();
                    Toast.makeText(this, "You must enable location permission to access the weather" ,Toast.LENGTH_LONG).show();
                }
                break;
        }
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
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        startLocationUpdates();
                        break;
                    case Activity.RESULT_CANCELED:
                        break;
                }
                break;
        }
    }

    private void getWeatherData(){
        getLocationInformation();
        initaliseLoader();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
        getLoaderManager().destroyLoader(WEATHER_LOADER_ID);
        loadWeatherData ();
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

    @Override
    public void onLocationChanged(Location location) {

        mCurrentLocation = location;

        getWeatherData();

        stopLocationUpdates();
    }


    @Override
    public void onConnectionFailed(ConnectionResult result) {

    }

    private void initaliseLoader(){

        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            LoaderManager loaderManager = getLoaderManager();

            loaderManager.initLoader(WEATHER_LOADER_ID, null, this);
        } else {
            Toast.makeText(this, "Your device needs internet access for this operation", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadWeatherData () {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            boolean done = false;
            boolean isLocationUsed = getApplicationContext().getSharedPreferences("my_sources", Context.MODE_PRIVATE).getBoolean(getResources().getString(R.string.location_switch_pref), true);
            String storedLocation = getApplicationContext().getSharedPreferences("my_sources", Context.MODE_PRIVATE).getString(getResources().getString(R.string.location_pref), "");
            if (!isLocationUsed) {
                if (!storedLocation.equals("")) {
                    Gson gson = new Gson();
                    com.example.hamidur.mynews.model.Location myLocation = gson.fromJson(storedLocation, com.example.hamidur.mynews.model.Location.class);
                    mCurrentLocation = new Location(myLocation.getAsciiName());
                    mCurrentLocation.setLatitude(myLocation.getLat());
                    mCurrentLocation.setLongitude(myLocation.getLng());
                    setLocationHeaderInformation(null, myLocation);
                    done = true;
                } else {
                    AlertDialog.Builder dialog = createDialog(getResources().getString(R.string.need_default_loc), false);

                    dialog.setPositiveButton(
                            getResources().getString(R.string.enable_default_location),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    startActivity(new Intent(WeatherActivity.this, LocationActivity.class));
                                }
                            });

                    dialog.setNegativeButton(
                            getResources().getString(R.string.Cancel),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    finish();
                                    dialog.cancel();
                                }
                            });

                    dialog.show();
                }

            } else {
                checkLocationSettings();
            }
            if (done) {
                initaliseLoader();
            }
        } else {
            AlertDialog.Builder dialog = createDialog(getResources().getString(R.string.need_wifi), false);

            dialog.setPositiveButton(
                    getResources().getString(R.string.enable_wifi),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                        }
                    });

            dialog.setNegativeButton(
                    getResources().getString(R.string.Cancel),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            finish();
                            dialog.cancel();
                        }
                    });

            dialog.show();
        }
    }

    @Override
    public void onLoadFinished(Loader<List<Weather>> loader, List<Weather> data) {

        RelativeLayout weatherBck = (RelativeLayout) findViewById(R.id.current_weather_bck);
        weatherBck.setBackground(ResourcesCompat.getDrawable(getResources(), setWeatherBack(data.get(0).getDescriptionCode()), null));
        // setting current weather from first item in the list
        TextView date = (TextView) findViewById(R.id.weather_date);
        date.setText(data.get(0).getDate());

        TextView day = (TextView) findViewById(R.id.weather_day);
        day.setText(data.get(0).getDay());

        TextView description = (TextView) findViewById(R.id.weather_description);
        description.setText(data.get(0).getDescription() + " " + data.get(0).getMinTemp() + "\u00B0" + " - " + data.get(0).getMaxTemp() + "\u00B0");

        ImageView currentDayIcon = (ImageView) findViewById(R.id.weather_icon);
        Glide.with(this).load("http://www.weatherunlocked.com/Images/icons/1/" + data.get(0).getImgUrl()).into(currentDayIcon);

        TextView temp = (TextView) findViewById(R.id.weather_temp);
        temp.setText(data.get(0).getTemp()+ "\u00B0");
        temp.setVisibility(View.VISIBLE);

        ProgressBar progressBar = (ProgressBar) findViewById(R.id.loading_indicator);
        progressBar.setVisibility(View.GONE);

        // setting 7-days forcast data
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        forcastAdapter = new WeatherAdapter(this, data);

        RecyclerView forcastView = (RecyclerView) findViewById(R.id.weather_forcast_list);
        forcastView.setLayoutManager(layoutManager);
        forcastView.setAdapter(forcastAdapter);
    }

    private AlertDialog.Builder createDialog (String msg, boolean cancelable) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(WeatherActivity.this);
        builder1.setMessage(msg);
        builder1.setCancelable(cancelable);
        return builder1;
    }

    @Override
    public Loader<List<Weather>> onCreateLoader(int id, Bundle args) {
        Uri baseUri = Uri.parse("https://api.weatherunlocked.com/api/trigger/" + mCurrentLocation.getLatitude() + "," + mCurrentLocation.getLongitude() + "/forecast%20tomorrow%20temperature%20gt%2016%20include7dayforecast?app_id=47c57285&app_key=4a3d79d727c3af86ede4b3dbc14f3555");
        return new WeatherLoader(this, baseUri.toString());
    }

    @Override
    public void onLoaderReset(Loader<List<Weather>> loader) {
    }

    private boolean getLocationInformation(){

        try {
            Geocoder coder = new Geocoder(getApplicationContext(), Locale.getDefault());

            if (mCurrentLocation != null) {
                List<Address> addresses = coder.getFromLocation(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude(), 1);
                if (!addresses.isEmpty()) {
                    setLocationHeaderInformation(addresses, null);
                    return true;
                }
            }
        } catch (SecurityException s){
            System.out.println("SecurityException Permission denied");
        } catch (IOException io){
            System.out.println("IOException Permission denied");
        }
        return false;
    }

    private void setLocationHeaderInformation (List<Address> addresses, com.example.hamidur.mynews.model.Location location) {
        TextView countryCode = (TextView) findViewById(R.id.weather_country);
        TextView city = (TextView) findViewById(R.id.weather_city);
        if (addresses != null && location == null) {
            if(addresses.get(0).getLocality().length() >= 25)
                city.setTextSize(14);

            countryCode.setText(addresses.get(0).getCountryCode());
            city.setText(addresses.get(0).getLocality());
        } else {
            if(location.getAsciiName().length() >= 25)
                city.setTextSize(18);

            countryCode.setText(location.getCountryCode());
            city.setText(location.getAsciiName());
        }
    }

    private int setWeatherBack (int weatherCode) {
        switch (weatherCode) {
            // sunny
            case  0 :
                return R.drawable.weather_bck_sunny_clr;
            // cloudy
            case 1 : case 2 : case 3 : case 21 : case 22 : case 23 : case 24 : case 29 :
                return R.drawable.weather_bck_cloudy;
            // rain
            case 50 : case 51 : case 56 : case 57 : case 60 :  case 61 :  case 62 :  case 63 :  case 64 :  case 65 :  case 66 :  case 67 :
            case 80 :   case 81 :   case 82 :
                return R.drawable.weather_bck_rain;
            // snow/sleet
            case 38 : case 39 : case 68 : case 69 : case 70 :  case 71 :  case 72 :  case 73 :  case 75 :  case 79 : case 83 :
            case 84 :  case 85 :  case 86 :  case 87 :  case 88 :
                return R.drawable.weather_bck_snw_clr;
            // mist/fog
            case 45 : case 49 :
                return R.drawable.weather_bck_mist;
            // storm
            case 91 : case 92 : case 93 : case 94 :
                return R.drawable.weather_bck_storm;
            default: return R.drawable.weather_bck_cloudy;
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    private class WeatherAdapter extends  RecyclerView.Adapter<WeatherAdapter.ViewHolder> {
        private List<Weather> allForcast;
        private Context context;
        public WeatherAdapter(Context context, List<Weather> weathers) {
            allForcast = weathers;
            this.context = context;
        }

        @Override
        public WeatherAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);

            View forcastView = inflater.inflate(R.layout.forcast_list_item, parent, false);

            ViewHolder viewHolder = new ViewHolder(forcastView);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(WeatherAdapter.ViewHolder holder, int position) {
            Weather weather = allForcast.get(position);
            holder.forcastDay.setText(weather.getDay());
            Glide.with(WeatherActivity.this).load(IMG_URL +weather.getImgUrl()).into(holder.forcastIcon);
            holder.forcastTemps.setText(weather.getMinTemp() + "\u00B0" + " - " + weather.getMaxTemp()+ "\u00B0");
        }

        @Override
        public int getItemCount() {
            return allForcast.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private TextView forcastDay;
            private ImageView forcastIcon;
            private TextView forcastTemps;

            public ViewHolder(View itemView) {
                super(itemView);
                forcastDay = (TextView) itemView.findViewById(R.id.forcast_day);
                forcastIcon = (ImageView) itemView.findViewById(R.id.forcast_icon);
                forcastTemps = (TextView) itemView.findViewById(R.id.forcast_min_max);
            }
        }

    }
}
