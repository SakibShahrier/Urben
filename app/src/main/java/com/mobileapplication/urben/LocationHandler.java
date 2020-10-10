package com.mobileapplication.urben;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class LocationHandler {

    private ArrayList<Integer> result = new ArrayList<Integer>();

    public ArrayList<Integer> distanceBetween(String a, String b, ArrayList<String> ref){
        int i = 1, j = 1;
        boolean m = true, n = true;

        for(String x : ref){
            if(!(x.equals(a)) && m){
                ++i;
            }else {
                m = false;
            }

            if (!(x.equals(b)) && n){
                ++j;
            }else {
                n = false;
            }
        }

        result.add(i);
        result.add(j);
        result.add(Math.abs(i - j));

        return result;
    }

    public String routeName(int start, int end){
        String route = "";

        if(start > end){
            route = "route_two";
        }else if(start < end){
            route = "route_one";
        }else{
            route = "kidding";
        }

        return route;
    }

    public int locationPosition(String a, ArrayList<String> ref){
        int i = 0;
        for(String x : ref){
            ++i;
            if(x.equals(a)){
                return i;
            }
        }
        return 0;
    }

    public LatLng getLatLongByPosition(int i, ArrayList<String> ref){
        Log.d("getLatlngByposition", "Found i :" + i);
        Log.d("getLatlngByposition", ref.get(i));
        String[] temp = ref.get(i).split(",");
        Log.d("getLatlngByposition", "Found : " + temp[0] + " " + temp[1]);
        LatLng l = new LatLng(Double.parseDouble(temp[0]), Double.parseDouble(temp[1]));

        return l;
    }

    public double calDistance(LatLng one, LatLng two){

        double latOne = one.latitude, latTwo = two.latitude, longOne = one.longitude, longTwo = two.longitude;
        double longDiff = longOne - longTwo;

        double distance = (Math.sin((latOne * Math.PI / 180.0)) * Math.sin((latTwo * Math.PI / 180.0))) +
                (Math.cos((latOne * Math.PI / 180.0)) * Math.cos((latTwo * Math.PI / 180.0)) * Math.cos((longDiff * Math.PI / 180.0)));

        distance = (distance * 180.0 / Math.PI);
        distance = distance * 1.61;

        return distance;
    }
}
