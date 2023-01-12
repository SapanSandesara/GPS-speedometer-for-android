package com.example.gpsspeedmeter;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

import androidx.annotation.NonNull;



public class myLocationListener implements LocationListener {
    private MainActivity mainactivity;

    public myLocationListener(MainActivity ma){
        this.mainactivity = ma;
    }
    @Override
    public void onLocationChanged(@NonNull Location location) {
        //Calls the calculate speed on mainactivity class using the location object
        this.mainactivity.calculatespeed(location);
    }


    @Override
    public void onProviderEnabled(String provider) {
        // This method is called when the provider is enabled by the user.
    }

    @Override
    public void onProviderDisabled(String provider) {
        // This method is called when the provider is disabled by the user.
    }
}
