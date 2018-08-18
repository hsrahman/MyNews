package com.example.hamidur.mynews.model;

/**
 * Created by Naziur on 17/08/2018.
 */

public class ExchangeRate {

    private String from, to, val;

    public ExchangeRate(String from,String to,String val){
        this.from = from;
        this.to = to;
        this.val = val;
    }

    public String getFrom(){
        return from;
    }

    public String getTo(){
        return to;
    }

    public String getVal(){
        return val;
    }
}
