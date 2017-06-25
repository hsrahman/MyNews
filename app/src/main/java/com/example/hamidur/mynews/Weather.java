package com.example.hamidur.mynews;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by pro-developer Hamidur on 25/06/2017.
 */
public class Weather {

    private String day, date, description, imgUrl;

    private int minTemp, maxTemp, temp;

    public Weather(String date, double minTemp, double maxTemp, double temp, String description, String imgUrl) {
        this.date = date;
        this.minTemp = removeDecimal(minTemp);
        this.maxTemp = removeDecimal(maxTemp);;
        this.description = description;
        this.imgUrl = imgUrl;
        this.temp = removeDecimal(temp);;

        SimpleDateFormat inFormat = new SimpleDateFormat("dd/MM/yyyy");
        try{
            Date dateAsDate = inFormat.parse(date);
            SimpleDateFormat outFormat = new SimpleDateFormat("EEEE");
            this.day = outFormat.format(dateAsDate);
        }catch(ParseException p){

        }

    }

    private int removeDecimal (double value) {
        return (int) Math.round(value);
    }

    public int getTemp(){
        return temp;
    }

    public String getDay() {
        return day;
    }

    public String getDate() {
        return date;
    }

    public int getMinTemp() {
        return minTemp;
    }

    public int getMaxTemp() {
        return maxTemp;
    }

    public String getDescription() {
        return description;
    }

    public String getImgUrl() {
        return imgUrl;
    }
}
