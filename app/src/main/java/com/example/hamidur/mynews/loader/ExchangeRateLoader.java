package com.example.hamidur.mynews.loader;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.example.hamidur.mynews.model.ExchangeRate;
import com.example.hamidur.mynews.utility.QueryUtils;

import java.util.List;

/**
 * Created by Naziur on 17/08/2018.
 */

public class ExchangeRateLoader extends AsyncTaskLoader<List<ExchangeRate>> {

    private String mUrl;

    public ExchangeRateLoader(Context context, String mUrl) {
        super(context);
        this.mUrl = mUrl;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<ExchangeRate> loadInBackground() {
        if (mUrl == null) {
            return null;
        }
        return QueryUtils.fetchExchangeRateDate(mUrl);
    }
}
