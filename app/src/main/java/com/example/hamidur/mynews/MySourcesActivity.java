package com.example.hamidur.mynews;

import android.content.Context;
import android.support.v4.util.ArraySet;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.example.hamidur.mynews.adapter.SourceAdapter;
import com.example.hamidur.mynews.model.Source;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MySourcesActivity extends AppCompatActivity {

    private SourceAdapter mAdapter;
    private final Gson gson = new Gson();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_sources);

        ListView orderViewList = (ListView) findViewById(R.id.order_by_list);
        List<Source> options = new ArrayList<>();
        Set<String> prefSource = getSharedPreferences("my_sources", Context.MODE_PRIVATE).getStringSet(getResources().getString(R.string.source_pref), new ArraySet<String>());

        //((TextView) findViewById(R.id.empty_view)).setText(R.string.no_sources_selected);

        for(String s : prefSource){
            Source source = gson.fromJson(s, Source.class);
            options.add(source);
        }

        mAdapter = new SourceAdapter(this, options);

        orderViewList.setEmptyView(findViewById(R.id.empty_view));

        /*orderViewList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Dialog dialog = new Dialog(MySourcesActivity.this);
                dialog.setContentView(R.layout.dialog);

                ImageView close_btn = (ImageView) dialog.findViewById(R.id.dialog_close_btn);

                close_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                final Set<String> prefs = getSharedPreferences("my_sources", Context.MODE_PRIVATE).getStringSet(getResources().getString(R.string.source_pref), new ArraySet<String>());
                for(final String s : prefs){
                   final Source source = gson.fromJson(s, Source.class);
                    if(source.getId().equals(((Source) parent.getItemAtPosition(position)).getId())){
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
                                        editor.putStringSet(getResources().getString(R.string.source_pref), prefs);
                                        editor.commit();
                                    }
                                });
                            }
                        }
                    }
                }
                dialog.show();
            }
        });*/

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
