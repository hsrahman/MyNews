package com.example.hamidur.mynews;

import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
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

import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class WeatherActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Weather>>{

    private String WEATHERAPI_REQUEST_URL_WEATHER = "https://api.weatherunlocked.com/api/trigger/51.50,-0.12/forecast%20tomorrow%20temperature%20gt%2016%20include7dayforecast?app_id=47c57285&app_key=4a3d79d727c3af86ede4b3dbc14f3555";
    private String IMG_URL = "http://www.weatherunlocked.com/Images/icons/1/";

    private static final int WEATHER_LOADER_ID = 3;

    private WeatherAdapter forcastAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);


        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if(networkInfo != null && networkInfo.isConnected()){
            LoaderManager loaderManager = getLoaderManager();

            loaderManager.initLoader(WEATHER_LOADER_ID, null, this);
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
        Uri baseUri = Uri.parse(WEATHERAPI_REQUEST_URL_WEATHER);
        return new WeatherLoader(this, baseUri.toString());
    }

    @Override
    public void onLoaderReset(Loader<List<Weather>> loader) {
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
