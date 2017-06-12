package com.example.hamidur.mynews;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

/**
 * Created by Naziur the greatest developer ever on 12/06/2017.
 */
public class NewsAdapter extends ArrayAdapter<NewsArticle>{


    public NewsAdapter(Context context, List<NewsArticle> article) {
        super(context, 0, article);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Check if there is an existing list item view (called convertView) that we can reuse,
        // otherwise, if convertView is null, then inflate a new list item layout.
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.news_article_list_item, parent, false);
        }

        return listItemView;
    }
}
