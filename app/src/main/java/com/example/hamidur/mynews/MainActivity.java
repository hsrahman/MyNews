package com.example.hamidur.mynews;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button news = (Button) findViewById(R.id.news);
        Button clear = (Button) findViewById(R.id.reset);
        news.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newsActivity = new Intent(MainActivity.this, NewsActivity.class);
                startActivity(newsActivity);
            }
        });

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("my_sources", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.remove("source");
                editor.clear();
                editor.commit();
                Toast.makeText(getApplicationContext(), "removed preferences: " + getApplicationContext().getSharedPreferences("my_sources", Context.MODE_PRIVATE).getString("source", getString(R.string.my_source)), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.action_bar_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.settings:
                Intent sourceActivity = new Intent(MainActivity.this, SourceActivity.class);
                startActivity(sourceActivity);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
