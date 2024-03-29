package com.example.hamidur.mynews.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.hamidur.mynews.R;
import com.example.hamidur.mynews.model.ExchangeRate;
import com.example.hamidur.mynews.utility.QueryUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Hamidur on 17/08/2018.
 */

public class ExchangeAdapter extends ArrayAdapter<ExchangeRate> {

    private OnSpinnerItemSelectedListener onSpinnerItemSelectedListener;

    private List<String> allCurrencyCodes;
    private List<String> countryCurrency;

    private List<TextView> allFields;

    public ExchangeAdapter(Context context, List<ExchangeRate> exchangeRates) {
        super(context, 0, exchangeRates);
        allFields = new ArrayList<>();
        readJsonCurrencies ();
    }

    public interface OnSpinnerItemSelectedListener{
        void onSpinnerItemSelected(int spinnerId, String spinnerData);
    }

    public void setOnSpinnerItemSelectedListener(OnSpinnerItemSelectedListener onSpinnerItemSelectedListener){
        this.onSpinnerItemSelectedListener = onSpinnerItemSelectedListener;
    }

    @Override
    public View getView(final int positionInList, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.exchange_rate_item, parent, false);
        }

        ExchangeRate rate = getItem(positionInList);

        final TextView countryExrName = (TextView) listItemView.findViewById(R.id.ex_country_name);
        final TextView countryExrValue = (TextView) listItemView.findViewById(R.id.ex_rate);
        final ImageView countryImage = (ImageView) listItemView.findViewById(R.id.countryIcon);
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_item,
                allCurrencyCodes);
        allFields.add(countryExrValue);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner s = (Spinner) listItemView.findViewById(R.id.country_ex_spinner);
        s.setAdapter(adapter);
        s.setSelection(adapter.getPosition(rate.getTo()));
        setItemCountryIcon(rate.getTo(), countryImage);
        s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            int count=0;
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(count >= 1) {
                    onSpinnerItemSelectedListener.onSpinnerItemSelected(positionInList, parent.getSelectedItem().toString());
                    countryExrName.setText(countryCurrency.get(position));
                    countryExrValue.setText("0");
                    setItemCountryIcon(parent.getSelectedItem().toString(), countryImage);
                }
                count++;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        countryExrName.setText(countryCurrency.get(adapter.getPosition(rate.getTo())));
        countryExrValue.setText(rate.getVal());

        return listItemView;
    }

    private void setItemCountryIcon(String currencyCode, ImageView img){
        Glide.with(getContext()).load("http://fxtop.com/ico/"+currencyCode.toLowerCase()+".gif")
                .override(100,50)
                .centerCrop()
                .error(R.drawable.ic_unknown_country)
                .into(img);
    }

    public void resetAllCurrencyValues () {
        for (TextView v : allFields) {
            v.setText("0");
        }
    }

    private void readJsonCurrencies () {
        allCurrencyCodes = new ArrayList<>();
        countryCurrency = new ArrayList<>();
        try {
            JSONObject currencyObj = new JSONObject(QueryUtils.readFromRawJsonFile(getContext().getResources(),R.raw.currencies));
            JSONObject result = currencyObj.getJSONObject("results");
            Iterator<?> currencyCodes = result.keys();
            while( currencyCodes.hasNext() ) {
                String key = (String)currencyCodes.next();
                if ( result.get(key) instanceof JSONObject ) {
                    allCurrencyCodes.add(key);
                    countryCurrency.add(result.getJSONObject(key).getString("currencyName"));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String> getAllCurrencyCodes () {
        return allCurrencyCodes;
    }

    public List<String> getCurrencyNames () {
        return countryCurrency;
    }

}
