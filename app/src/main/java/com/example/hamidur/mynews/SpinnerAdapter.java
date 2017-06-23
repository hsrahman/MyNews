package com.example.hamidur.mynews;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.List;

/**
 * Created by Naziur the greatest developer ever on 22/06/2017.
 */
public class SpinnerAdapter extends ArrayAdapter<String> {


    public SpinnerAdapter(Context context, List<String> sources) {
        super(context,0, sources);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        return setUpLayoutView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {

        return setUpLayoutView(position, convertView, parent);
    }

    private View setUpLayoutView(int position, View convertView, ViewGroup parent){
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.spinner_item, parent, false);
        }

        String s = getItem(position);

        ImageView sourceIcon = (ImageView) listItemView.findViewById(R.id.source_icon);
        sourceIcon.setImageResource(setIconId(s));
        TextView category = (TextView) listItemView.findViewById(R.id.category);
        category.setText(s);

        return listItemView;
    }

    private int setIconId (String category) {
        switch (category) {
            case "science-and-nature":
                return R.drawable.ic_nature;
            case "gaming":
                return R.drawable.ic_game;
            case "music":
                return R.drawable.ic_music;
            case "politics":
                return R.drawable.ic_politics;
            case "technology":
                return R.drawable.ic_tech;
            case "sport":
                return R.drawable.ic_sport;
            case "entertainment":
                return R.drawable.ic_entertainment;
            case "business":
                return R.drawable.ic_business;
            default:
                return R.drawable.ic_general;
        }
    }
}
