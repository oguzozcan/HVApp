package com.armut.armuthv.services;

import android.app.Activity;
import android.util.Log;

import com.armut.armuthv.ArmutHVApp;
import com.armut.armuthv.busevents.MiscEvents;
import com.armut.armuthv.fragments.DialogOpenLocation;
import com.squareup.otto.Subscribe;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by oguzemreozcan on 11/11/16.
 */

public class LocationPopupService {

    private final String TAG = "LocationPopupService";
    private final ArmutHVApp app;
    private Activity activity;
    private final DialogOpenLocation openLocationDialog;

    public LocationPopupService(Activity activity) {
        this.activity = activity;
        this.app = (ArmutHVApp) activity.getApplication();
        openLocationDialog = DialogOpenLocation.newInstance();
    }

    public LocationPopupService(ArmutHVApp app) {
        this.app = app;
        openLocationDialog = DialogOpenLocation.newInstance();
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public void onPause() {
        Log.d(TAG, "ON PAUSE LOC");
        try {
            app.getBus().unregister(this);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
    }

    public void onResume() {
        Log.d(TAG, "ON RESUME LOC");
        try {
            app.getBus().register(this);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
    }

    @Subscribe
    public void displayTurnOnLocationServicePopup(MiscEvents.DisplayTurnOnLocationServicePopupRequest request) {
        Log.d(TAG, "Display Turn on Location Service Popup");
        //if(!app.isLocationServiceAvailable){
        FusedLocationer fusedLocationer = app.getFusedLocationer();
        fusedLocationer.checkLocationSettings();
        //}
    }

    @Subscribe
    public void displayBlockingLocationServicePopup(MiscEvents.DisplayBlockingLocationPopupRequest request) {
        Log.d(TAG, "Display Turn on Location BLOCKING Service Popup : " + app.isLocationServiceAvailable);
        if (activity != null) {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!app.isLocationServiceAvailable || !app.isLocationPremissionGranted) {
                                if (openLocationDialog.isAdded()) {
                                    Log.d(TAG, "OPEN LOCATION DIALOG IS ALREADY ADDED");
                                    openLocationDialog.dismiss();
                                }
                                openLocationDialog.show(activity.getFragmentManager(), "fragment_open_location");
                                //TODO never ask again
//                        if(!app.isLocationPremissionGranted){
//                            ArmutUtils.openApplicationSettings(activity);
//                        }
                            } else {
                                openLocationDialog.dismiss();
                            }
                        }
                    });
                }
            }, 100);
        }
    }
}
