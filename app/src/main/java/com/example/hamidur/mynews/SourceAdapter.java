package com.example.hamidur.mynews;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.ArraySet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by Naziur the greatest developer ever on 15/06/2017.
 */
public class SourceAdapter extends ArrayAdapter<Source> {

    private final Gson gson = new Gson();

    public SourceAdapter(Context context, List<Source> sources) {
        super(context, 0, sources);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

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

        TextView country = (TextView) listItemView.findViewById(R.id.source_country);
        country.setText("Country: " + source.getCountry().toUpperCase());

        ImageView icon = (ImageView) listItemView.findViewById(R.id.source_icon);
        icon.setImageResource(source.getIconId());

        if(getContext().getClass() == OrderByActivity.class){
            System.out.println("This is the OrderByActivity class");
            ImageView removeSource = (ImageView) listItemView.findViewById(R.id.remove_source);
            removeSource.setTag(position);
            removeSource.setVisibility(View.VISIBLE);
            removeSource.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Set<String> prefs = getContext().getSharedPreferences("my_sources", Context.MODE_PRIVATE).getStringSet("source", new ArraySet<String>());
                    for (Iterator<String> iterator = prefs.iterator(); iterator.hasNext();){
                        Source s = gson.fromJson(iterator.next(), Source.class);
                        if (s.getId().equals(getItem(position).getId())) {
                            iterator.remove();
                            break;
                        }
                    }

                    getItem(position).setSelected(false);
                    SharedPreferences sharedPref = getContext().getSharedPreferences("my_sources", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.remove("source");
                    editor.commit();
                    editor.putStringSet("source", prefs);
                    editor.commit();
                    remove(getItem(position));
                    notifyDataSetChanged();
                }
            });
        } else {
            if (source.isSelected()) listItemView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.selected_source));
            else listItemView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.unselected_source));
        }

        return listItemView;
    }
}
