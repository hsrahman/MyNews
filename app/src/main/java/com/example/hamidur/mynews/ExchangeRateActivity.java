package com.example.hamidur.mynews;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.example.hamidur.mynews.adapter.ExchangeAdapter;
import com.example.hamidur.mynews.loader.ExchangeRateLoader;
import com.example.hamidur.mynews.model.ExchangeRate;

import java.util.List;

public class ExchangeRateActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<ExchangeRate>>, ExchangeAdapter.OnSpinnerItemSelectedListener{

    private static final String ER_URL = "https://free.currencyconverterapi.com/api/v6/convert";

    private static final int EXCHANGE_RATE_LOADER_ID = 4;
    private String country1 = "DKK", country2 = "GBP";
    private ExchangeAdapter exchangeAdapter;
    public ListView listView;
    private View loadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exchange_rate);
        listView = (ListView) findViewById(R.id.exchangerates_list);
        loadingIndicator = (View) findViewById(R.id.loading_indicator);
        LoaderManager loaderManager = getLoaderManager();

        loaderManager.initLoader(EXCHANGE_RATE_LOADER_ID, null, this);
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    public Loader<List<ExchangeRate>> onCreateLoader(int id, Bundle args) {
        //String userSelectedCurrency = getApplicationContext().getSharedPreferences("my_sources", Context.MODE_PRIVATE).getString(getResources().getString(R.string.currency), "");
        String userSelectedCurrency = "USD";
        Uri baseUri = Uri.parse(ER_URL + "?q="+userSelectedCurrency+"_"+country1+","+userSelectedCurrency+"_"+country2);
        return new ExchangeRateLoader(this, baseUri.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<ExchangeRate>> loader, List<ExchangeRate> data) {
        loadingIndicator.setVisibility(View.GONE);
        exchangeAdapter = new ExchangeAdapter(this, data);
        listView.setAdapter(exchangeAdapter);
        exchangeAdapter.setOnSpinnerItemSelectedListener(this);
    }

    @Override
    public void onLoaderReset(Loader<List<ExchangeRate>> loader) {
        if(exchangeAdapter != null)
            exchangeAdapter.clear();
    }

    @Override
    public void onSpinnerItemSelected(String spinnerId, String spinnerData) {
        switch(spinnerId){
            case ExchangeAdapter.SPINNER_1_ID:
                country1 = spinnerData;
                break;
            case ExchangeAdapter.SPINNER_2_ID:
                country2 = spinnerData;
                break;
        }
    }
}
