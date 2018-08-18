package com.example.hamidur.mynews;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Spinner;

import com.example.hamidur.mynews.loader.ExchangeRateLoader;
import com.example.hamidur.mynews.model.ExchangeRate;

import java.util.List;

public class ExchangeRateActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<ExchangeRate>>{

    private static final String ER_URL = "https://free.currencyconverterapi.com/api/v6/convert";

    private static final int EXCHANGE_RATE_LOADER_ID = 4;
    private String country1, country2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exchange_rate);
    }

    @Override
    protected void onStart() {
        super.onStart();
        getLoaderManager().destroyLoader(EXCHANGE_RATE_LOADER_ID);
    }

    @Override
    public Loader<List<ExchangeRate>> onCreateLoader(int id, Bundle args) {
        String userSelectedCurrency = getApplicationContext().getSharedPreferences("my_sources", Context.MODE_PRIVATE).getString(getResources().getString(R.string.currency), "");
        Uri baseUri = Uri.parse(ER_URL + "?q="+userSelectedCurrency+"_");
        return new ExchangeRateLoader(this, baseUri.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<ExchangeRate>> loader, List<ExchangeRate> data) {

    }

    @Override
    public void onLoaderReset(Loader<List<ExchangeRate>> loader) {

    }
}
