package com.example.hamidur.mynews;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by Naziur the greatest developer ever on 15/06/2017.
 */
public class SourceAdapter extends ArrayAdapter<Source> {

    public SourceAdapter(Context context, List<Source> sources) {
        super(context, 0, article);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Check if there is an existing list item view (called convertView) that we can reuse,
        // otherwise, if convertView is null, then inflate a new list item layout.
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.source_list_item, parent, false);
        }

        Source source = getItem(position);

        TextView name = (TextView) listItemView.findViewById(R.id.source_name);

        name.setText(source.getName());

        return listItemView;
    }
}
