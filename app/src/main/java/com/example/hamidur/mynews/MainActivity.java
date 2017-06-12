package com.example.hamidur.mynews;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button news = (Button) findViewById(R.id.news);
        news.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newsActivity = new Intent(MainActivity.this, NewsActivity.class);
                startActivity(newsActivity);
            }
        });
    }
}
