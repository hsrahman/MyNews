package com.example.hamidur.mynews;

import java.io.Serializable;

/**
 * Created by pro-developer Hamidur on 12/06/2017.
 */
public class NewsArticle {

    private String title, date, description, author ,imgUrl, sourceUrl;

    public NewsArticle(String imgUrl, String description, String title, String author , String date, String sourceUrl) {
        this.imgUrl = imgUrl;
        this.description = description;
        this.title = title;
        this.date = date;
        this.author = author;
        this.sourceUrl = sourceUrl;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public String getDescription() {
        return description;
    }

    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }

    public String getAuthor () { return author; }

    public String getSourceUrl () { return sourceUrl; }
}
