package com.example.hamidur.mynews;

import android.text.TextUtils;
import android.util.Log;

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

                if (author == null) author = "";
                if (date == null)date = "";
                if (imgUrl == null || imgUrl == "") imgUrl = "https://electricalwholesalersperth.com.au/wp-content/uploads/2016/02/unavailable.jpg";

                NewsArticle newsArticle = new NewsArticle(imgUrl, description, title, author, date, url);

                newsArticleList.add(newsArticle);
            }

        } catch (JSONException e) {
            Log.e("QueryUtils", "Problem parsing the article JSON results", e);
        }

        return newsArticleList;
    }

    public static List<NewsArticle> fetchNewsArticleData(String requestUrl){
        URL url = createUrl(requestUrl);

        String jsonResponse = null;

        try{
            jsonResponse = createHttpRequest(url);
        } catch (IOException e){
            Log.e(LOG_TAG, "Problem making the HTTP request");
        }

        List<NewsArticle> newsArticles = extractNewsFromJson(jsonResponse);
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
                sourcesList.add(new Source(id, name, category));
            }
        } catch (JSONException e) {
            Log.e("QueryUtils", "Problem parsing the source JSON results", e);
        }
        return sourcesList;
    }

    public static List<Source> fetchSourceData(String requestUrl){
        URL url =  createUrl(requestUrl);
        String jsonResponse = null;
        try {
            jsonResponse = createHttpRequest(url);
        } catch (IOException io) {
            Log.e(LOG_TAG, "Problem making the HTTP request");
        }
        List<Source> sources = extractSourceFromJson(jsonResponse);
        return sources;
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
                urlConnection.connect();
                if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    inputStream = urlConnection.getInputStream();
                    jsonResponse = readFromStream(inputStream);
                } else{
                    Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
                }
            } catch (IOException e) {
                Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
            } finally {
                if (urlConnection != null) urlConnection.disconnect();
                if (inputStream != null) inputStream.close();
            }
        }
        return jsonResponse;
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
}
