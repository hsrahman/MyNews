package com.example.hamidur.mynews;

import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.jar.Manifest;

public class WeatherActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Weather>>{

    private static final String IMG_URL = "http://www.weatherunlocked.com/Images/icons/1/";

    LocationManager locationManager;

    private static final int REQUEST_CODE = 1;

    private static final int WEATHER_LOADER_ID = 3;

    private Location location;

    private WeatherAdapter forcastAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        int locationMode = 0;
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        try {
            locationMode = Settings.Secure.getInt(this.getContentResolver(), Settings.Secure.LOCATION_MODE);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        if (locationMode == Settings.Secure.LOCATION_MODE_OFF) {
            Intent homeActivity = new Intent(WeatherActivity.this, MainActivity.class);
            startActivity(homeActivity);
            Toast.makeText(this, "Your location service is not enabled" ,Toast.LENGTH_SHORT).show();
        } else {
            ConnectivityManager connMgr = (ConnectivityManager)
                    getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

            if(networkInfo != null && networkInfo.isConnected()){
                LoaderManager loaderManager = getLoaderManager();

                loaderManager.initLoader(WEATHER_LOADER_ID, null, this);
            }
        }


    }

    @Override
    public void onLoadFinished(Loader<List<Weather>> loader, List<Weather> data) {

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

        // setting 7-days forcast data
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        forcastAdapter = new WeatherAdapter(this, data);

        RecyclerView forcastView = (RecyclerView) findViewById(R.id.weather_forcast_list);
        forcastView.setLayoutManager(layoutManager);
        forcastView.setAdapter(forcastAdapter);
    }

    @Override
    public Loader<List<Weather>> onCreateLoader(int id, Bundle args) {
        setLocationInformation ();
        Uri baseUri = Uri.parse("https://api.weatherunlocked.com/api/trigger/"+ location.getLatitude() +","+ location.getLongitude() +"/forecast%20tomorrow%20temperature%20gt%2016%20include7dayforecast?app_id=47c57285&app_key=4a3d79d727c3af86ede4b3dbc14f3555");
        return new WeatherLoader(this, baseUri.toString());
    }

    @Override
    public void onLoaderReset(Loader<List<Weather>> loader) {
    }

    private void setLocationInformation () {
        if (checkPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            getLocationInformation();
            Toast.makeText(this, "Permission allowed", Toast.LENGTH_LONG).show();
        } else {
            requestPermission(android.Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    private boolean checkPermission (String permission) {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), permission);
        return (result == PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermission (String permission) {
        //This code requests permission once and in future manual location activation
        /*if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)){
            Toast.makeText(this, "GPS permission allows us to access your location, please allow in app settings for additional functionality", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(this, new String[] {permission}, REQUEST_CODE);
        }*/
        ActivityCompat.requestPermissions(this, new String[] {permission}, REQUEST_CODE);
    }

    private void getLocationInformation(){

        try {
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            Geocoder coder = new Geocoder(getApplicationContext(), Locale.getDefault());

            if (location != null) {
                List<Address> addresses = coder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                if (!addresses.isEmpty()) {
                    TextView countryCode = (TextView) findViewById(R.id.weather_country);
                    countryCode.setText(addresses.get(0).getCountryCode());
                    TextView city = (TextView) findViewById(R.id.weather_city);
                    city.setText(addresses.get(0).getLocality());
                } else {
                    Toast.makeText(this, "Failed to get any locations for Lat: " + location.getLatitude() + " and Long: " + location.getLongitude(), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Failed to get any location information", Toast.LENGTH_SHORT).show();
                //locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,5000, 20, listener);
            }
        } catch (SecurityException s){
            System.out.println("SecurityException Permission denied");
        } catch (IOException io){
            System.out.println("IOException Permission denied");
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE :
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLocationInformation();
                    Toast.makeText(this, "Permission allowed", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_LONG).show();
                }
                break;
        }
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
