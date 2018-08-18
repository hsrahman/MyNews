package com.example.hamidur.mynews.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.example.hamidur.mynews.R;
import com.example.hamidur.mynews.model.ExchangeRate;
import com.example.hamidur.mynews.utility.QueryUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Hamidur on 17/08/2018.
 */

public class ExchangeAdapter extends ArrayAdapter<ExchangeRate> {

    private OnSpinnerItemSelectedListener onSpinnerItemSelectedListener;
    public static final String SPINNER_1_ID = "SPINNER_1";
    public static final String SPINNER_2_ID = "SPINNER_2";

    public ExchangeAdapter(Context context, List<ExchangeRate> exchangeRates) {
        super(context, 0, exchangeRates);
    }

    public interface OnSpinnerItemSelectedListener{
        void onSpinnerItemSelected(String spinnerId, String spinnerData);
    }

    public void setOnSpinnerItemSelectedListener(OnSpinnerItemSelectedListener onSpinnerItemSelectedListener){
        this.onSpinnerItemSelectedListener = onSpinnerItemSelectedListener;
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
