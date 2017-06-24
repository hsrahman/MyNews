package com.example.hamidur.mynews;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Naziur the greatest developer ever on 24/06/2017.
 */
public class SettingsAdapter extends ArrayAdapter<String> {


    public SettingsAdapter(Context context, List<String> settings) {
        super(context, 0, settings);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Check if there is an existing list item view (called convertView) that we can reuse,
        // otherwise, if convertView is null, then inflate a new list item layout.
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.settings_items, parent, false);
        }

        ImageView settingIcon = (ImageView) listItemView.findViewById(R.id.setting_icon);
        TextView settingsText = (TextView) listItemView.findViewById(R.id.setting_text);

        settingsText.setText(getItem(position));
        settingIcon.setImageResource(getSettingResourceId(getItem(position)));
        return listItemView;
    }

    private int getSettingResourceId(String option){
        switch(option){
            case "Change Sources":
                return R.drawable.ic_add_circle;
            case "My Source":
                return R.drawable.ic_border_color;
            case "Clear All Sources":
                return R.drawable.ic_delete_sweep;
            default: return R.id.setting_icon;
        }
    }
}
