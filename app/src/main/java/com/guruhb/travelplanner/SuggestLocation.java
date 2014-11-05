package com.guruhb.travelplanner;

import android.location.Location;

/**
 * Created by guruhb@gmail.com on 04-11-2014.
 */
public class SuggestLocation {
    private String name;
    private String fullName;
    private String type;
    private GeoPosition geoPosition;
    private String countryCode;
    private long distance;

    public SuggestLocation() {
        name = "";
        fullName = "";
        type = "";
        geoPosition = new GeoPosition(0.0, 0.0); //Fixme : set default location
        countryCode = "";
        distance = 0;
    }
    public SuggestLocation(String name, String fullName, String type, double lat, double lng, String countryCode, long distance) {
        this.name = name;
        this.fullName = fullName;
        this.type = type;
        this.geoPosition = new GeoPosition(lat, lng); //Fixme : set default location
        this.countryCode = countryCode;
        this.distance = distance;
    }

    public class GeoPosition {
        public double lat;
        public double lng;
        public GeoPosition(double latitude, double longitude) {
            this.lat = latitude;
            this.lng = longitude;
        }
    }

    public String getName() { return name; }
    public String getFullName() { return fullName; }

}
