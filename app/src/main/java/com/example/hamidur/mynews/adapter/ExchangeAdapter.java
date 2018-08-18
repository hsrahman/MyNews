package com.example.hamidur.mynews.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.hamidur.mynews.R;
import com.example.hamidur.mynews.utility.QueryUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Iterator;

/**
 * Created by Hamidur on 17/08/2018.
 */

public class ExchangeAdapter extends ArrayAdapter<ExchangeRate> {

    public ExchangeAdapter(Context context, List<ExchangeRate> exchangeRates) {
        super(context, 0, exchangeRates);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.exchange_rate_item, parent, false);
        }

        ExchangeRate rate = getItem(position);



        return listItemView;
    }


    private void readJsonCurrencies () {
        try {
            JSONObject currencyObj = new JSONObject(QueryUtils.readFromRawJsonFile(getContext().getResources(),R.raw.currencies));
            JSONObject result = currencyObj.getJSONObject("results");
            Iterator<?> allCurrencyCodes = result.keys();
            while( allCurrencyCodes.hasNext() ) {
                String key = (String)allCurrencyCodes.next();
                if ( result.get(key) instanceof JSONObject ) {

                }
            }


        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
