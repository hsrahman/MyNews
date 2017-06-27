package com.example.hamidur.mynews;

/**
 * Created by pro-developer Hamidur on 27/06/2017.
 */
public class Location {

    private String countryCode, timeZoneId, asciiName;
    private double lat, lng;

    public Location(double lng, double lat, String countryCode, String timeZoneId, String asciiName) {
        this.lng = lng;
        this.lat = lat;
        this.countryCode = countryCode;
        this.timeZoneId = timeZoneId;
        this.asciiName = asciiName;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public String getTimeZoneId() {
        return timeZoneId;
    }

    public String getAsciiName() {
        return asciiName;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }
}
