package com.example.hamidur.mynews;

/**
 * Created by Naziur the greatest developer ever on 15/06/2017.
 */
public class Source {

    private String category;

    private String name;

    private String country;

    private String language;

    private String id;

    private String url;

    private String description;

    private String[] sortByAvailable;

    private boolean selected;

    public Source(String id, String name, String category, String[] sortByAvailable) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.sortByAvailable = sortByAvailable;
        selected = false;
    }

    public void setSelected (boolean status) {
        selected = status;
    }

    public boolean isSelected() { return selected; }

    public String getCategory() {
        return category;
    }

    public String getName() {
        return name;
    }

    public String getCountry() {
        return country;
    }

    public String getLanguage() {
        return language;
    }

    public String getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public String getDescription() {
        return description;
    }

    public String[] getSortByAvailable() {
        return sortByAvailable;
    }
}
