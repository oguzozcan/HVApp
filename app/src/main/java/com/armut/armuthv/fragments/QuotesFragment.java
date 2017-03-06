package com.armut.armuthv.fragments;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.armut.armuthv.busevents.JobEvents;
import com.armut.armuthv.busevents.MiscEvents;
import com.armut.armuthv.utils.Constants;
import com.squareup.otto.Subscribe;

/**
 * Created by oguzemreozcan on 06/06/16.
 */

public class QuotesFragment extends JobBasicFragment {

    public QuotesFragment() {
        // Required empty public constructor
    }

    //@Override
    public static QuotesFragment newInstance(int sectionNumber) {
        QuotesFragment fragment = new QuotesFragment();
        Bundle args = new Bundle();
        args.putInt(JobBasicFragment.ARG_SECTION_NUMBER, sectionNumber);
        Log.d("QuotesFragment", "SECTION NUM: " + sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Log.d(TAG, "SECTION NUM: " + sectionNumber);
            sectionNumber = getArguments().getInt(ARG_SECTION_NUMBER, 0);
        }
    }

    @Override
    protected void setTag() {
        TAG = "QuotesFragment";
    }


    @Override
    public void onStart() {
        super.onStart();
        String token = app.isTokenPresent(TAG);
        if (token != null && isJobListViewEmpty()) {
            Log.d(TAG, "ON START OF QUOTES FRAGMENT");
            app.getBus().post(new JobEvents.QuotesRequest(token, ""));
        }
    }

    @Subscribe
    public void onLoadQuotes(JobEvents.QuotesResponse response) {
        Log.d(TAG, "ON LOAD Quotes");
        setJobsListView(response.getResponse());
    }

    @Subscribe
    public void onNotificationReceived(MiscEvents.NotificationReceivedEvent event){
        try{
            if(event != null && getActivity() != null){
                String token = app.isTokenPresent(TAG);
                if(token != null) {
                    app.getBus().post(new JobEvents.QuotesRequest(token, ""));
                }
                Log.d(TAG, "REFRESH QUOTES FEED AND CANCEL NOTIFICATION: " + event.jobId);
                NotificationManager notificationManager = (NotificationManager)getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancel(event.jobId);
            }
        }catch (Exception e){
            e.printStackTrace();
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
        String token = app.isTokenPresent(TAG);
        if (token != null) {
            if (requestCode == Constants.UPDATE_JOBS_PAGE) {
                //TODO write token to the storage get it and refresh the request also show loading UI
                app.getBus().post(new JobEvents.QuotesRequest(token, ""));
            } else if (requestCode == Constants.NEW_LOGIN_UPDATE_ALL) {
                updateAll(token);
            }
        }
    }
}




