package com.mobileapplication.urben;

import android.util.Log;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Random;

public class Trip implements Serializable {

    private static final double AC_FARE = 15; // BDT for Stoppage to Stoppage
    private static final double NON_AC_FARE = 10; // BDT for Stoppage to Stoppage
    private String startLocation;
    private String endLocation;
    private double fare;
    private double rating;
    private String complain;
    private String routeName;
    private String ticketID;
    private String email;
    private String passengerName;
    private String serviceType;

    public Trip(){

    }

    public Trip(String startLocation, String endLocation, double fare, String routeName, String email, String passengerName, String serviceType){
        this.startLocation = startLocation;
        this.endLocation = endLocation;
        this.fare = fare;
        this.routeName = routeName;
        this.serviceType = serviceType;
        this.email = email;
        this.passengerName = passengerName;
        rating = 0.0;
        complain = "None";

        ticketID = "#" + endLocation.toUpperCase() + new Random().nextInt(100000);
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setTicketID(String ticketID) {
        this.ticketID = ticketID;
    }

    public void setFare(double fare) {
        this.fare = fare;
    }

    public String getTicketID() {
        return ticketID;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public static double getAcFare() {
        return AC_FARE;
    }

    public static double getNonAcFare() {
        return NON_AC_FARE;
    }

    public double getFare() {
        return fare;
    }

    public double getRating() {
        return rating;
    }

    public String getComplain() {
        return complain;
    }

    public String getEndLocation() {
        return endLocation;
    }

    public String getStartLocation() {
        return startLocation;
    }

    public void setComplain(String complain) {
        this.complain = complain;
    }

    public void setEndLocation(String endLocation) {
        this.endLocation = endLocation;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public void setTicketID() {
        ticketID = "#" + endLocation.toUpperCase() + new Random().nextInt(100000);
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassengerName(String passengerName) {
        this.passengerName = passengerName;
    }

    public String getEmail() {
        return email;
    }

    public String getPassengerName() {
        return passengerName;
    }

    public void setStartLocation(String startLocation) {
        this.startLocation = startLocation;
    }

    @Override
    public String toString() {
        return "Trip{" +
                "startLocation='" + startLocation + '\'' +
                ", endLocation='" + endLocation + '\'' +
                ", fare=" + fare +
                ", rating=" + rating +
                ", complain='" + complain + '\'' +
                ", routeName='" + routeName + '\'' +
                ", ticketID='" + ticketID + '\'' +
                '}';
    }

    public double calFare(int distance, String serviceType){

        Log.d("Trip", "calFare() Distance Found :" + distance);
        double totalFare = 0.00;
        if(serviceType.equals("AC")){
            totalFare = distance * AC_FARE;
        }else if(serviceType.equals("NON_AC")){
            totalFare = distance * NON_AC_FARE;
        }
        Log.d("Trip", "calFare() Fare Sent:" + totalFare);
        return totalFare;
    }
}
