package com.armut.armuthv.services;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.armut.armuthv.ArmutHVApp;
import com.armut.armuthv.utils.ArmutUtils;
import com.armut.armuthv.utils.Constants;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

/**
 * Created by oguzemreozcan on 02/11/16.
 */

public class FusedLocationer implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ResultCallback<LocationSettingsResult> { // LocationListener,
    //TODO refactor this
    private GoogleApiClient mGoogleApiClient;
    private PendingIntent mPendingIntent;
    //    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final int INTERVAL = 120000; // 2 minutes 30000;//
    private static final int MIN_DISPLACEMENT = 10; // as meters
    static final double ACCU_THRESHOLD = 100.0;
    private Activity activity;
    private final String TAG = "FusedLocationer";
    private LocationSettingsRequest mLocationSettingsRequest;
    private LocationRequest mLocationRequest;

    public void startService(Activity activity) {
        if (activity != null) {
            this.activity = activity;
            Intent mIntentService = new Intent(activity, FusedLocationService.class);
            mPendingIntent = PendingIntent.getService(activity, 1, mIntentService, 0);
            initGoogleApiClient(activity);
        }
    }

    private synchronized void initGoogleApiClient(Activity activity) {
        if (ArmutUtils.checkPlayServices(activity)) {
            mGoogleApiClient = new GoogleApiClient.Builder(activity)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
            mGoogleApiClient.connect();
        } else {
            Toast.makeText(activity, "Google Play Service Error ", Toast.LENGTH_LONG).show();
        }
        createLocationRequest();
        buildLocationSettingsRequest();
    }

    public void requestLastLocation() throws SecurityException {
        if (isLocationPermissionGranted()) {
            if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                Location loc = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                Log.i(TAG, "Last Known Location :" + loc.getLatitude() + "," + loc.getLongitude());
                //txtLastKnownLoc.setText(loc.getLatitude() + "," + loc.getLongitude());
            }
        }
    }

    void checkLocationSettings() {
        if (isLocationPermissionGranted()) {
            if (mGoogleApiClient != null && mGoogleApiClient.isConnected() && mLocationSettingsRequest != null) {
                PendingResult<LocationSettingsResult> result =
                        LocationServices.SettingsApi.checkLocationSettings(
                                mGoogleApiClient,
                                mLocationSettingsRequest
                        );
                result.setResultCallback(this);
            } else {
                if (activity != null)
                    initGoogleApiClient(activity);
            }
        }
    }

    //Google Example
    private void createLocationRequest() {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(INTERVAL); //5000
        mLocationRequest.setFastestInterval(INTERVAL);
        Log.d(TAG, "LOCATION PROVIDER PRIORITY: " + mLocationRequest.getPriority());
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        //locationrequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(MIN_DISPLACEMENT);
    }

    private void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        //TODO
        builder.setAlwaysShow(true);
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }

    public void requestLocationInBackground() throws SecurityException {
        if (isLocationPermissionGranted()) {
            if (mGoogleApiClient != null && mGoogleApiClient.isConnected() && mLocationRequest != null) {
//                LocationRequest locationrequest = LocationRequest.create();
//                locationrequest.setInterval(INTERVAL); //5000
//                locationrequest.setFastestInterval(INTERVAL);
//                Log.d(TAG, "LOCATION PROVIDER PRIORITY: " + locationrequest.getPriority());
//                locationrequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
//                //locationrequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//                locationrequest.setSmallestDisplacement(MIN_DISPLACEMENT);
                Log.d(TAG, "REQUEST LOCATION UPDATE");
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, mPendingIntent);
            } else {
                Log.d(TAG, "GOOGLE API CLIENT NOT CONNETCTED YET");
            }
        }
    }

    private boolean isLocationPermissionGranted() {
        if (activity != null) {
            if (ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                ActivityCompat.requestPermissions(activity,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION},
                        Constants.PERMISSIONS_REQUEST_LOCATION);
                return false;
            }
            try{
                ArmutHVApp app = (ArmutHVApp) activity.getApplication();
                if(app != null){
                    app.isLocationPremissionGranted = true;
                    app.getDataSaver().putBoolean(Constants.PERMISSION_KEY_REQUEST_LOCATION, true);
                    app.getDataSaver().save();
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        return true;
    }

    //TODO
    public void onActivityDestroyed() {
        Log.d(TAG, "ON ACTIVITY DESTROYED");
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
            activity = null;
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "onConnected");
        //TODO
        if (activity != null) {
            ArmutHVApp app = (ArmutHVApp) activity.getApplication();
            if (!app.isLocationServiceAvailable) {
                checkLocationSettings();
            }
        } else {
            checkLocationSettings();
        }
        //getCurrentLocationSetting(LocationRequest.create());
        //requestLocationInBackground();
        //txtConnectionStatus.setText("Connection Status : Connected");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "onConnectionSuspended");
//        txtConnectionStatus.setText("Connection Status : Suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG, "onConnectionFailed");
//        txtConnectionStatus.setText("Connection Status : Fail");
    }

//    @Override
//    public void onLocationChanged(Location location) {
//        if (location != null) {
//            Log.d(TAG, "LOCATION ACCURACY: " + location.getAccuracy());
//            if (location.getAccuracy() < ACCU_THRESHOLD) {
//                double lon = location.getLongitude();
//                double lat = location.getLatitude();
//                Log.i(TAG, "LOCATION Request : lat,lon: " + lat + "," + lon);
//                long mLastLocationMillis = SystemClock.elapsedRealtime();
//
//                if(app.isTokenPresent(TAG) != null){
//                    Coordinate coordinate = new Coordinate();
//                    coordinate.setLatitude(lat);
//                    coordinate.setLongitude(lon);
//                    coordinate.setRecordDate(TimeUtils.getDateTimeFromMillis(mLastLocationMillis));
//                    Log.d(TAG, "LOCATION Sent to the api.");
//                    app.getBus().post(new MiscEvents.PostCoordinateRequest(coordinate));
//                }
//    //        intent.putExtra("lasttime", mLastLocationMillis);
//    //        intent.putExtra("lat", location.getLatitude());
//    //        intent.putExtra("lon", location.getLongitude());
//    //        LocalBroadcastManager.getInstance(ctx).sendBroadcast(intent);
//                // TODO Do something.
//                //  insertLocation(location);
//                //txtLocationRequest.setText(location.getLatitude() + "," + location.getLongitude());
//            } else {
//                Log.e(TAG, "LOCATION IS NOT VERY ACCURATE");
//            }
//        }
//    }

//    private void insertLocation(Location location) {
//        if (activity != null) {
//            double longitude;
//            double latitude;
//            String time;
//            String extra;
//            String result = "Location currently unavailable.";
//            // get coordinates
//            longitude = location.getLongitude();
//            latitude = location.getLatitude();
//            time = MyUtility.parseTime(location.getTime());
//            extra = dumpLocation(location);
//            result = Double.toString(latitude) + ", " + Double.toString(longitude);
//            // put them into db
//            ContentValues values = new ContentValues();
//            values.put(LocTable.COLUMN_TIME, time);
//            values.put(LocTable.COLUMN_LATITUDE, latitude);
//            values.put(LocTable.COLUMN_LONGITUDE, longitude);
//            values.put(LocTable.COLUMN_EXTRA, extra);
//            activity.getContentResolver().insert(LocContentProvider.CONTENT_URI, values);
//            Log.d(TAG, "put in value " + result + " " + extra);
//            Toast.makeText(activity, result, Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    /**
//     * Describe the given location, which might be null
//     */
//    private String dumpLocation(Location location) {
//        String msg;
//        StringBuilder builder = new StringBuilder();
//        builder.append("P:")
//                .append(location.getProvider())
//                .append("|A:")
//                .append(location.getAccuracy());
//        msg = builder.toString();
//        return msg;
//    }

    @Override
    public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
        final Status status = locationSettingsResult.getStatus();
        if(activity == null){
            return;
        }
        //final LocationSettingsStates state = result.getLocationSettingsStates();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
                // All location settings are satisfied. The client can
                // initialize location requests here.
                Log.d(TAG, "SUCCESS");
                if (activity != null) {
                    ArmutHVApp app = (ArmutHVApp) activity.getApplication();
                    app.isLocationServiceAvailable = true;
                    requestLocationInBackground();
                }
                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                Log.d(TAG, "RESOLUTION_REQUIRED");
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    status.startResolutionForResult(
                            activity,
                            Constants.REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException e) {
                    // Ignore the error.
                }
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                // Location settings are not satisfied. However, we have no way
                // to fix the settings so we won't show the dialog.
                Log.d(TAG, "SETTINGS_CHANGE_UNAVAILABLE");
                break;
        }
    }
}
