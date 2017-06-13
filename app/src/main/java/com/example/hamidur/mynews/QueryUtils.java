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

/**
 * Created by pro-developer Hamidur on 12/06/2017.
 */
public class QueryUtils {
    private static String LOG_TAG = "EXCEPTION";

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {

    }


    private static List<NewsArticle> extractFeaturesFrpmJson(String articleJson){
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

                NewsArticle newsArticle = new NewsArticle(imgUrl, description, title, author, date);

                newsArticleList.add(newsArticle);
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
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

        List<NewsArticle> newsArticles = extractFeaturesFrpmJson(jsonResponse);
        return newsArticles;

    }


}
