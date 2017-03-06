package com.armut.armuthv.fragments;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.adjust.sdk.Adjust;
import com.adjust.sdk.AdjustEvent;
import com.armut.armuthv.busevents.JobEvents;
import com.armut.armuthv.busevents.MiscEvents;
import com.armut.armuthv.utils.Constants;
import com.squareup.otto.Subscribe;

/**
 * Created by oguzemreozcan on 24/05/16.
 */
public class OpportunitiesFragment extends JobBasicFragment {

    public OpportunitiesFragment() {
    }

//    @Override
    public static OpportunitiesFragment newInstance(int sectionNumber) {
        OpportunitiesFragment fragment = new OpportunitiesFragment();
        Bundle args = new Bundle();
        args.putInt(JobBasicFragment.ARG_SECTION_NUMBER, sectionNumber);
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(app.isTokenPresent(TAG) != null){
            AdjustEvent event = new AdjustEvent("koja2z"); //Viewed Opportunities
            Adjust.trackEvent(event);
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected void setTag() {
        TAG = "OpportunitiesFragment";
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "ON START OF OPPORTUNITIES");
        String token = app.isTokenPresent(TAG);
        if(token != null && isJobListViewEmpty()) {
            app.getBus().post(new JobEvents.OpportunitiesRequest(token, ""));
        }
    }

    @Subscribe
    public void onLoadOpportunuties(JobEvents.OpportunitiesResponse response) {
        //Log.d(TAG, "ON LOAD OPPORTUNITIES success" + response.getResponse().isSuccessful());
        if(response.getResponse() != null){
            Log.d(TAG, "ON LOAD OPPORTUNITIES size" + response.getResponse().body().size());
            setJobsListView(response.getResponse());
        }
    }

    @Subscribe
    public void onNotificationReceived(MiscEvents.NotificationReceivedEvent event){
        try{
            if(event != null && getActivity() != null){
                String token = app.isTokenPresent(TAG);
                if(token != null) {
                    app.getBus().post(new JobEvents.OpportunitiesRequest(token, ""));
                }
                Log.d(TAG, "REFRESH OPPORTUNITIES FEED AND CANCEL NOTIFICATION: " + event.jobId);
                NotificationManager notificationManager = (NotificationManager)getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancel(event.jobId);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "ON ACTIVITY RESULT opportunities fragment requestCode: " + requestCode + " - resultCode: " + resultCode);
        //super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            //TODO handle error
            //loadingCallback.loadingIsInProgress(false, null);
            return;
        }
        String token = app.isTokenPresent(TAG);
        if(token != null) {
            if (requestCode == Constants.UPDATE_JOBS_PAGE) {
                //TODO write token to the storage get it and refresh the request also show loading UI
                app.getBus().post(new JobEvents.OpportunitiesRequest(token, ""));
            } else if (requestCode == Constants.NEW_LOGIN_UPDATE_ALL || requestCode == Constants.CHANGE_OPPORTUNITY_TO_QUOTE_PAGE) {
                Log.d(TAG, "UPDATE ALL");
                updateAll(token);
            }
        }
    }
}
