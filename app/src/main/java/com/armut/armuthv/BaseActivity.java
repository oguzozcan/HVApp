package com.armut.armuthv;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.armut.armuthv.busevents.MiscEvents;
import com.armut.armuthv.utils.Constants;
import com.armut.armuthv.utils.DataSaver;

public abstract class BaseActivity extends AppCompatActivity {

    protected String TAG = "BaseActivity";
    protected ArmutHVApp app;
    protected DataSaver ds;
    protected boolean isActive;

//    protected FusedLocationBoundService mBoundService;
//    protected boolean mServiceBound = false;

    protected abstract void setTag();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = (ArmutHVApp) getApplication();
        ds = app.getDataSaver();
        setTag();
    }

    @Override
    protected void onStart() {
        super.onStart();
        app.popupService.setActivity(this);
        app.popupService.onResume();

    }
//
    @Override
    protected void onStop() {
        super.onStop();
        app.popupService.onPause();
    }

    @Override
    protected void onPause() {
        super.onPause();
        isActive = false;
        Log.d(TAG, "BASE ACT  ON PAUSE");
//        app.getBus().register(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        isActive = true;
        //popupService.onResume();
        Log.d(TAG, "BASE ACT  ON RESUME");
//        app.getBus().unregister(this);
    }

    //Should be called after setContentView so each activity shoudl call this seperatly
    public void initActionBar(String title){
        ActionBar mActionBar = getSupportActionBar();
        if(mActionBar != null){
            mActionBar.setDisplayShowHomeEnabled(false);
            mActionBar.setDisplayShowTitleEnabled(false);
            LayoutInflater mInflater = LayoutInflater.from(this);
            View mCustomView = mInflater.inflate(R.layout.custom_actionbar, null);
            TextView mTitleTextView = (TextView) mCustomView.findViewById(R.id.toolbar_title);
            mTitleTextView.setText(title);
            mActionBar.setCustomView(mCustomView);
            mActionBar.setDisplayShowCustomEnabled(true);
        }else{
            Log.d(TAG, "ACTIONBAR NULL");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }

    public static void setTranslateAnimation(Activity activity) {
        activity.overridePendingTransition(R.anim.enter_from_right, R.anim.exit_from_left);
    }

    public static void setBackwardsTranslateAnimation(Activity activity) {
        activity.overridePendingTransition(R.anim.enter_from_left, R.anim.exit_from_right);
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            finish();
            setBackwardsTranslateAnimation(this);
        }
    }

    boolean displayBlockingLocationPopup = false;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == Constants.REQUEST_CHECK_SETTINGS){
            Log.d(TAG, "REQUEST CHECK SETTINGS");
            if(resultCode == Activity.RESULT_OK){
                app.isLocationServiceAvailable = true;
                app.getFusedLocationer().requestLocationInBackground();
            }else{
                //TODO add popup here
                app.isLocationServiceAvailable = false;
                displayBlockingLocationPopup = true;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if(displayBlockingLocationPopup){
            app.getBus().post(new MiscEvents.DisplayBlockingLocationPopupRequest());
            displayBlockingLocationPopup = false;
        }
    }

    //    protected ServiceConnection mServiceConnection = new ServiceConnection() {
//
//        @Override
//        public void onServiceDisconnected(ComponentName name) {
//            mServiceBound = false;
//        }
//
//        @Override
//        public void onServiceConnected(ComponentName name, IBinder service) {
//            FusedLocationBoundService.MyBinder myBinder = (FusedLocationBoundService.MyBinder) service;
//            mBoundService = myBinder.getService();
//            mServiceBound = true;
//        }
//    };
}
