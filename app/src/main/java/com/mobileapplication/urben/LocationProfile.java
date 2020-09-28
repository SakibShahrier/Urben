package com.mobileapplication.urben;

public class LocationProfile {
    private String places, coordinates;
    private int position;

    public LocationProfile() {

    }

    public LocationProfile(String places, String coordinates, int position) {
        this.places = places;
        this.coordinates = coordinates;
        this.position = position;
    }

    public String getPlaces() {
        return places;
    }

    public void setPlaces(String places) {
        this.places = places;
    }

    public String getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(String coordinates) {
        this.coordinates = coordinates;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
