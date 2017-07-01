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

    private String currentSortBy;

    private String[] sortByAvailable;

    private boolean selected;

    private int iconId;

    public Source(String id, String name, String category, String country ,String[] sortByAvailable) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.sortByAvailable = sortByAvailable;
        this.country = country;
        currentSortBy = sortByAvailable[0];
        setIconId ();
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

    public String getCurrentSortBy() { return currentSortBy; }

    public String[] getSortByAvailable() {
        return sortByAvailable;
    }

    public void setCurrentSortBy(String currentSortBy) {
        this.currentSortBy = currentSortBy;
    }

    public int getIconId() { return iconId; }

    public void setIconId () {
        switch (category) {
            case "science-and-nature" :
                iconId = R.drawable.ic_nature;
                break;
            case "gaming" :
                iconId = R.drawable.ic_game;
                break;
            case "music" :
                iconId = R.drawable.ic_music;
                break;
            case "politics" :
                iconId = R.drawable.ic_politics;
                break;
            case "technology" :
                iconId = R.drawable.ic_tech;
                break;
            case "sport" :
                iconId = R.drawable.ic_sport;
                break;
            case "entertainment" :
                iconId = R.drawable.ic_entertainment;
                break;
            case "business" :
                iconId = R.drawable.ic_business;
                break;
            default: iconId = R.drawable.ic_general;
        }
    }
}
