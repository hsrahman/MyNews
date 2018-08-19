package com.example.hamidur.mynews;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.hamidur.mynews.adapter.ExchangeAdapter;
import com.example.hamidur.mynews.loader.ExchangeRateLoader;
import com.example.hamidur.mynews.model.ExchangeRate;
import com.example.hamidur.mynews.utility.QueryUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ExchangeRateActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<ExchangeRate>>, ExchangeAdapter.OnSpinnerItemSelectedListener{

    private static final String ER_URL = "https://free.currencyconverterapi.com/api/v6/convert";

    private static final int EXCHANGE_RATE_LOADER_ID = 4;
    private List<String> countries;
    private ExchangeAdapter exchangeAdapter;
    public ListView listView;
    private View loadingIndicator;

    private ImageView myCountryIcon;
    private TextView myCountryValue;
    private Spinner myCountryName;
    private String userSelectedCurrency;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exchange_rate);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        listView = (ListView) findViewById(R.id.exchangerates_list);
        loadingIndicator = (View) findViewById(R.id.loading_indicator);
        myCountryIcon = (ImageView) findViewById(R.id.my_country_icon);
        myCountryName = (Spinner) findViewById(R.id.my_country_name);
        myCountryValue = (TextView) findViewById(R.id.my_country_code);

        Button getRateBtn = (Button) findViewById(R.id.get_rate_btn);

        countries = new ArrayList<>();
        countries.add("DKK");
        countries.add("GBP");
        getRateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(countries.isEmpty()){
                    countries.add("DKK");
                    countries.add("GBP");
                } else {
                    restartLoader();
                }
            }
        });

        LoaderManager loaderManager = getLoaderManager();

        loaderManager.initLoader(EXCHANGE_RATE_LOADER_ID, null, this);
    }

    private void restartLoader(){
        LoaderManager loaderManager = getLoaderManager();
        loaderManager.restartLoader(EXCHANGE_RATE_LOADER_ID, null, this);
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    public Loader<List<ExchangeRate>> onCreateLoader(int id, Bundle args) {
        userSelectedCurrency = getApplicationContext().getSharedPreferences("my_sources", Context.MODE_PRIVATE).getString(getResources().getString(R.string.currency), "");
        if(userSelectedCurrency.isEmpty())
            userSelectedCurrency = "USD";

        String queryString = "";
        for(int i = 0; i < countries.size(); i++){
            queryString += userSelectedCurrency+"_"+countries.get(i);

            if(i < countries.size()-1)
                queryString+=",";
        }
        Uri baseUri = Uri.parse(ER_URL + "?q="+queryString);
        return new ExchangeRateLoader(this, baseUri.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<ExchangeRate>> loader, final List<ExchangeRate> data) {
        loadingIndicator.setVisibility(View.GONE);
        exchangeAdapter = new ExchangeAdapter(this, data);
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(ExchangeRateActivity.this,
                android.R.layout.simple_spinner_item,
                exchangeAdapter.getCurrencyNames());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        myCountryName.setAdapter(adapter);
        myCountryName.setSelection(exchangeAdapter.getAllCurrencyCodes().indexOf(userSelectedCurrency));

        myCountryName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                userSelectedCurrency = exchangeAdapter.getAllCurrencyCodes().get(position);
                myCountryValue.setText("1 " + userSelectedCurrency);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        myCountryValue.setText("1 " + userSelectedCurrency);
        listView.setAdapter(exchangeAdapter);
        exchangeAdapter.setOnSpinnerItemSelectedListener(this);
        listView.setAdapter(exchangeAdapter);
        countries.clear();
    }

    @Override
    public void onLoaderReset(Loader<List<ExchangeRate>> loader) {
        if(exchangeAdapter != null)
            exchangeAdapter.clear();
    }

    @Override
    public void onSpinnerItemSelected(int spinnerId, String spinnerData) {
        countries.add(spinnerId, spinnerData);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home :
                finish();
                break;

            default:
                break;
        }

        return true;
    }

}
