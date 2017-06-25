package com.example.hamidur.mynews;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by pro-developer Hamidur on 25/06/2017.
 */
public class Weather {

    private String day, date, minTemp, maxTemp, temp, description, imgUrl;

    public Weather(String date, String minTemp, String maxTemp, String temp, String description, String imgUrl) {
        this.date = date;
        this.minTemp = minTemp;
        this.maxTemp = maxTemp;
        this.description = description;
        this.imgUrl = imgUrl;
        this.temp = temp;

        SimpleDateFormat inFormat = new SimpleDateFormat("dd/MM/yyyy");
        try{
            Date dateAsDate = inFormat.parse(date);
            SimpleDateFormat outFormat = new SimpleDateFormat("EEEE");
            this.day = outFormat.format(dateAsDate);
        }catch(ParseException p){

        }

    }

    public String getTemp(){
        return temp;
    }

    public String getDay() {
        return day;
    }

    public String getDate() {
        return date;
    }

    public String getMinTemp() {
        return minTemp;
    }

    public String getMaxTemp() {
        return maxTemp;
    }

    public String getDescription() {
        return description;
    }

    public String getImgUrl() {
        return imgUrl;
    }
}
