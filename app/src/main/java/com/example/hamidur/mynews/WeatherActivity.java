package com.example.hamidur.mynews;

import android.content.Context;
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

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class WeatherActivity extends AppCompatActivity {

    WeatherAdapter forcastAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        List<Weather> forcast = new ArrayList<>();
        forcast.add(new Weather("Monday") );
        forcast.add(new Weather("Tuesday") );
        forcast.add(new Weather("Wednesday") );
        forcast.add(new Weather("Thursday") );
        forcast.add(new Weather("Friday") );
        forcast.add(new Weather("Saturday") );
        forcast.add(new Weather("Sunday") );


        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        forcastAdapter = new WeatherAdapter(this, forcast);

        RecyclerView forcastView = (RecyclerView) findViewById(R.id.weather_forcast_list);
        forcastView.setLayoutManager(layoutManager);
        forcastView.setAdapter(forcastAdapter);


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
            TextView day = holder.forcastDay;
            day.setText(weather.getDay());
        }


        @Override
        public int getItemCount() {
            return allForcast.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private TextView forcastDay;

            public ViewHolder(View itemView) {
                super(itemView);

                forcastDay = (TextView) itemView.findViewById(R.id.forcast_day);

            }
        }

    }
}
