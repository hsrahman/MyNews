package com.example.hamidur.mynews;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.util.ArraySet;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class OrderByActivity extends AppCompatActivity {

    ArrayAdapter<String> mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_by);

        ListView orderViewList = (ListView) findViewById(R.id.order_by_list);
        List<String> options = new ArrayList<String>();
        Gson gson = new Gson();
        for(String s : getSharedPreferences("my_sources", Context.MODE_PRIVATE).getStringSet("source", new ArraySet<String>())){
            Source source = gson.fromJson(s, Source.class);
            options.add(source.getName());
        }

        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, options);

        orderViewList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Dialog dialog = new Dialog(OrderByActivity.this);
                dialog.setContentView(R.layout.dialog);
                final Gson gson = new Gson();

                ImageView close_btn = (ImageView) dialog.findViewById(R.id.dialog_close_btn);

                close_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                final Set<String> prefs = getSharedPreferences("my_sources", Context.MODE_PRIVATE).getStringSet("source", new ArraySet<String>());
                for(final String s : prefs){
                   final Source source = gson.fromJson(s, Source.class);
                    if(source.getName().equals(parent.getItemAtPosition(position))){
                        for(int i = 0; i < source.getSortByAvailable().length; i++){
                            int radioid = getCorrectOrderById(source.getSortByAvailable()[i]);
                            if(radioid != -1) {
                                final RadioButton r = (RadioButton) dialog.findViewById(radioid);
                                if (source.getCurrentSortBy().equals(r.getText().toString().toLowerCase())) r.setChecked(true);
                                r.setVisibility(View.VISIBLE);
                                r.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        source.setCurrentSortBy(r.getText().toString().toLowerCase());
                                        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("my_sources", Context.MODE_PRIVATE);
                                        SharedPreferences.Editor editor = sharedPref.edit();
                                        prefs.remove(s);
                                        prefs.add(gson.toJson(source));
                                        editor.clear();
                                        editor.putStringSet("source", prefs);
                                        editor.commit();
                                    }
                                });
                            }
                        }
                    }
                }
                dialog.show();
            }
        });

        orderViewList.setAdapter(mAdapter);

    }

    private int getCorrectOrderById(String orderBy){
        switch(orderBy){
            case "top":
                return R.id.top_radio_button;
            case "popular":
                return R.id.popular_radio_button;
            case "latest":
                return R.id.latest_radio_button;
        }

        return -1;
    }
}
