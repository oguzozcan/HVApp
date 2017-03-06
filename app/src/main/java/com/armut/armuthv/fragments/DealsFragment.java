package com.armut.armuthv.fragments;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.armut.armuthv.busevents.JobEvents;
import com.armut.armuthv.busevents.MiscEvents;
import com.armut.armuthv.utils.Constants;
import com.squareup.otto.Subscribe;

public class DealsFragment extends JobBasicFragment {

    public DealsFragment() {
    }

    @Override
    protected void setTag() {
        TAG = "DealsFragment";
    }

    // @Override
    public static DealsFragment newInstance(int sectionNumber) {
        DealsFragment fragment = new DealsFragment();
        Bundle args = new Bundle();
        args.putInt(JobBasicFragment.ARG_SECTION_NUMBER, sectionNumber);
        Log.d("DealsFragment", "SECTION NUM: " + sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Log.d(TAG, "SECTION NUM: " + sectionNumber);
            sectionNumber = getArguments().getInt(ARG_SECTION_NUMBER, 2);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "ON START OF DEALS FRAGMENT");
        String token = app.isTokenPresent(TAG);
        if (token != null && isJobListViewEmpty()) { //
            app.getBus().post(new JobEvents.DealsRequest(token, ""));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "ON ACTIVITY RESULT requestCode: " + requestCode + " - resultCode: " + resultCode);
        if (resultCode != Activity.RESULT_OK) {
            //TODO handle error
            //loadingCallback.loadingIsInProgress(false, null);
            return;
        }
        //TODO write token to the storage get it and refresh the request also show loading UI
        String token = app.isTokenPresent(TAG);
        if (token != null) {
            if (requestCode == Constants.UPDATE_JOBS_PAGE) {
                app.getBus().post(new JobEvents.DealsRequest(token, ""));
            } else if (requestCode == Constants.NEW_LOGIN_UPDATE_ALL) {
                updateAll(token);
            }
        }

    }

    @Subscribe
    public void onLoadDeals(JobEvents.DealsResponse response) {
        Log.d(TAG, "ON LOAD Deals");
        setJobsListView(response.getResponse());
    }

    @Subscribe
    public void onNotificationReceived(MiscEvents.NotificationReceivedEvent event){
        try{
            if(event != null && getActivity() != null){
                String token = app.isTokenPresent(TAG);
                if(token != null) {
                    app.getBus().post(new JobEvents.DealsRequest(token, ""));
                }
                Log.d(TAG, "REFRESH DEALS FEED AND CANCEL NOTIFICATION: " + event.jobId);
                NotificationManager notificationManager = (NotificationManager)getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancel(event.jobId);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}



