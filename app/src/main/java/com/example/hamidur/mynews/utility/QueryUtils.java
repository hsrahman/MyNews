package com.example.hamidur.mynews.utility;

import android.content.res.Resources;
import android.net.UrlQuerySanitizer;
import android.text.TextUtils;
import android.util.Log;

import com.example.hamidur.mynews.model.ExchangeRate;
import com.example.hamidur.mynews.model.Location;
import com.example.hamidur.mynews.model.NewsArticle;
import com.example.hamidur.mynews.model.Source;
import com.example.hamidur.mynews.model.Weather;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.nio.charset.Charset;

/**
 * Created by pro-developer Hamidur on 12/06/2017.
 */
public class QueryUtils {
    private static String LOG_TAG = "EXCEPTION";

    private QueryUtils() {

    }

    public static List<ExchangeRate> fetchExchangeRateDate(String requestUrl){
        List<ExchangeRate> locations = extractExchangeRateFromJson(getHttpResultString(requestUrl));
        return locations;
    }

    private static List<ExchangeRate> extractExchangeRateFromJson(String httpResultString) {
        if(TextUtils.isEmpty(httpResultString)) {
            return null;
        }

        List<ExchangeRate> exchangeRateList = new ArrayList<>();

        try{
            JSONObject baseJsonResponse = new JSONObject(httpResultString);
            JSONObject resultJson = baseJsonResponse.getJSONObject("results");

            UrlQuerySanitizer sanitizer = new UrlQuerySanitizer(httpResultString);
            String value = sanitizer.getValue("parameter");
            String[] valueArr = value.split(",");
            for (String currency: valueArr) {
                JSONObject exchangeRateJson = resultJson.getJSONObject(currency);
                exchangeRateList.add(new ExchangeRate(exchangeRateJson.getString("fr"), exchangeRateJson.getString("to"), exchangeRateJson.getString("val")));
            }

        } catch (JSONException e) {
            Log.e("QueryUtils", "Problem parsing the location JSON results", e);
        }

        return exchangeRateList;
    }


    private static List<Location> extractLocationsFromJson (String locationJson) {
        if(TextUtils.isEmpty(locationJson)) {
            return null;
        }

        List<Location> locationList = new ArrayList<>();

        try {

            JSONObject baseJsonResponse = new JSONObject(locationJson);

            JSONArray articleArray = baseJsonResponse.getJSONArray("geonames");
            for (int i = 0; i < articleArray.length(); i++) {
                String timeZoneId = articleArray.getJSONObject(i).getJSONObject("timezone").getString("timeZoneId");
                double lat = Double.parseDouble(articleArray.getJSONObject(i).getString("lat"));
                double lng = Double.parseDouble(articleArray.getJSONObject(i).getString("lng"));
                String countryCode = articleArray.getJSONObject(i).getString("countryCode");
                String asciiName = articleArray.getJSONObject(i).getString("asciiName");
                String continentCode = articleArray.getJSONObject(i).getString("continentCode");

                Location l = new Location(lat, lng, countryCode, timeZoneId, asciiName, continentCode);

                locationList.add(l);
            }

        }catch (JSONException e) {
            Log.e("QueryUtils", "Problem parsing the location JSON results", e);
        }
        return locationList;
    }

    public static List<Location> fetchLocationData(String requestUrl){
        List<Location> locations = extractLocationsFromJson(getHttpResultString(requestUrl));
        return locations;
    }

    private static List<NewsArticle> extractNewsFromJson(String articleJson){
        if(TextUtils.isEmpty(articleJson))
            return null;

        List<NewsArticle> newsArticleList = new ArrayList<>();

        try{

            JSONObject baseJsonResponse = new JSONObject(articleJson);

            JSONArray articleArray = baseJsonResponse.getJSONArray("articles");

            for(int i = 0; i < articleArray.length(); i++) {

                JSONObject currentArticle = articleArray.getJSONObject(i);

                String title = currentArticle.getString("title");
                String date = currentArticle.getString("publishedAt");
                String author = currentArticle.getString("author");
                String description = currentArticle.getString("description");
                String imgUrl = currentArticle.getString("urlToImage");
                String url = currentArticle.getString("url");

                if (author.equals("null")) author = "";
                if (date.equals("null"))date = "";
                if (description.equals("null")) description = "No description was provided (Click to read the story)";

                if (imgUrl.equals("null") || imgUrl.equals("")) imgUrl = "https://electricalwholesalersperth.com.au/wp-content/uploads/2016/02/unavailable.jpg";

                NewsArticle newsArticle = new NewsArticle(imgUrl, description, title, author, date, url);
                newsArticle.setTotal(Integer.parseInt(baseJsonResponse.getString("totalResults")));
                newsArticleList.add(newsArticle);
            }

        } catch (JSONException e) {
            Log.e("QueryUtils", "Problem parsing the article JSON results", e);
        }

        return newsArticleList;
    }

    public static List<NewsArticle> fetchNewsArticleData(String requestUrl){
        List<NewsArticle> newsArticles = extractNewsFromJson(getHttpResultString(requestUrl));
        return newsArticles;
    }

    private static List<Source> extractSourceFromJson(String sourceJson){
        if(TextUtils.isEmpty(sourceJson))
            return null;

        List<Source> sourcesList = new ArrayList<>();
        try {
            JSONObject baseJsonResponse = new JSONObject(sourceJson);

            JSONArray sourceArray = baseJsonResponse.getJSONArray("sources");
            for(int i = 0; i < sourceArray.length(); i++) {
                JSONObject currentSource = sourceArray.getJSONObject(i);
                String id = currentSource.getString("id");
                String name = currentSource.getString("name");
                String category = currentSource.getString("category");
                String country =  currentSource.getString("country");
                sourcesList.add(new Source(id, name, category, country));
            }
        } catch (JSONException e) {
            Log.e("QueryUtils", "Problem parsing the source JSON results", e);
        }
        return sourcesList;
    }

    public static List<Source> fetchSourceData(String requestUrl){
        List<Source> sources = extractSourceFromJson(getHttpResultString(requestUrl));
        return sources;
    }

    private static List<Weather> extractWeatherFromJson(String weatherJson){
        if(TextUtils.isEmpty(weatherJson))
            return null;

        List<Weather> weatherList = new ArrayList<>();

        try{
            JSONObject baseJsonResponse = new JSONObject(weatherJson);
            JSONObject forcastWeather = baseJsonResponse.getJSONObject("ForecastWeather");
            JSONArray days = forcastWeather.getJSONArray("Days");
            for(int i = 0; i < days.length(); i++) {
                JSONObject currentDay = days.getJSONObject(i);
                String date = currentDay.getString("date");
                double maxTemp = currentDay.getDouble("temp_max_c");
                double minTemp = currentDay.getDouble("temp_min_c");
                JSONArray timeFrame = currentDay.getJSONArray("Timeframes");
                JSONObject currentTimeFrame = timeFrame.getJSONObject(0);
                String description = currentTimeFrame.getString("wx_desc");
                int descriptionCode = currentTimeFrame.getInt("wx_code");
                String imgUrl = currentTimeFrame.getString("wx_icon");
                double currentTemp = currentTimeFrame.getDouble("temp_c");

                weatherList.add(new Weather(date, minTemp, maxTemp, currentTemp, description, imgUrl, descriptionCode));
            }

        } catch (JSONException e) {
            Log.e("QueryUtils", "Problem parsing the source JSON results", e);
        }

        return weatherList;

    }

    public static List<Weather> fetchWeatherData(String requestUrl){
        List<Weather> weathers = extractWeatherFromJson(getHttpResultString(requestUrl));
        return weathers;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    private static String createHttpRequest (URL url) throws IOException{
        String jsonResponse = "";
        if (url != null) {
            HttpURLConnection urlConnection = null;
            InputStream inputStream = null;
            try {
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setReadTimeout(10000);
                urlConnection.setConnectTimeout(15000);
                urlConnection.setRequestProperty("x-api-key","b5b4806ba6834681baecc6492d59d788");
                urlConnection.connect();
                if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    inputStream = urlConnection.getInputStream();
                    jsonResponse = readFromStream(inputStream);
                } else{
                    Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
                }
            } catch (IOException e) {
                Log.e(LOG_TAG, "Problem retrieving the JSON results.", e);
            } finally {
                if (urlConnection != null) urlConnection.disconnect();
                if (inputStream != null) inputStream.close();
            }
        }
        return jsonResponse;
    }

    public static String readFromRawJsonFile (final Resources resources, int resId) throws IOException{
        InputStream resourceReader = resources.openRawResource(resId);
        return readFromStream(resourceReader);
    }

    private static String readFromStream (InputStream inputStream) throws IOException{
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line;
            do {
                line = reader.readLine();
                output.append(line);
            } while (line != null);
        }
        return output.toString();
    }

    private static String getHttpResultString (String requestUrl) {
        URL url = createUrl(requestUrl);

        String jsonResponse = null;

        try{
            jsonResponse = createHttpRequest(url);
        } catch (IOException e){
            Log.e(LOG_TAG, "Problem making the HTTP request");
        }
        return jsonResponse;
    }
}
