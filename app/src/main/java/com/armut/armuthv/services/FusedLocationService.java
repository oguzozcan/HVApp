package com.armut.armuthv.services;

import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import android.os.Handler;
import android.util.Log;

import com.armut.armuthv.ArmutHVApp;
import com.armut.armuthv.busevents.MiscEvents;
import com.armut.armuthv.objects.Coordinate;
import com.armut.armuthv.utils.ArmutUtils;
import com.armut.armuthv.utils.TimeUtils;
import com.google.android.gms.location.LocationResult;

/**
 * Created by oguzemreozcan on 31/10/16.
 */

public class FusedLocationService extends IntentService {

    private final String TAG = this.getClass().getSimpleName();

    private long mLastLocationMillis;

    public FusedLocationService() {
        super("Fused Location");
    }
    public FusedLocationService(String name) {
        super("Fused Location");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        //Location location = intent.getParcelableExtra(FusedLocationProviderApi.KEY_LOCATION_CHANGED);
        if (LocationResult.hasResult(intent)) {
            LocationResult locationResult = LocationResult.extractResult(intent);
            Location location = locationResult.getLastLocation();
            if (location != null) {
                Log.i(TAG, "onHandleIntent " + location.getLatitude() + "," + location.getLongitude());
                onLocationChanged(location);
            }else{
                Log.d(TAG, "Location NUll");
            }
        }else{
            final ArmutHVApp app = (ArmutHVApp) getApplication();
            long timeInBetween = System.currentTimeMillis() - mLastLocationMillis;
            Log.d(TAG, "LOCATION HAS NO RESULT : timeInBetween getting succesful location " + timeInBetween );
            if(timeInBetween > 30000){
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        app.getBus().post(new MiscEvents.DisplayTurnOnLocationServicePopupRequest());
                        Log.d(TAG, "LOCATION HAS NO RESULT show turnonLocationServicePopupRequest");
                    }
                }, 2500);
            }
        }
    }

        @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    private void onLocationChanged(Location location){
        Log.d(TAG, "LOCATION ACCURACY: " + location.getAccuracy());
        mLastLocationMillis = System.currentTimeMillis();
        if (location.getAccuracy() < FusedLocationer.ACCU_THRESHOLD) {
            ArmutHVApp app = (ArmutHVApp) getApplication();
            double lon = location.getLongitude();
            double lat = location.getLatitude();
            Log.i(TAG, "LOCATION Request : lat,lon: " + lat + "," + lon + " - TIME: " + TimeUtils.getDateTimeFromMillis(mLastLocationMillis));
            if(app.isTokenPresent(TAG) != null && ArmutUtils.isNetworkAvailable(getBaseContext())){
                Coordinate coordinate = new Coordinate();
                coordinate.setLatitude(lat);
                coordinate.setLongitude(lon);
                coordinate.setRecordDate(TimeUtils.getDateTimeFromMillis(mLastLocationMillis));
                Log.d(TAG, "LOCATION Sent to the api.");
                app.getBus().post(new MiscEvents.PostCoordinateRequest(coordinate));
            }
            //        intent.putExtra("lasttime", mLastLocationMillis);
            //        intent.putExtra("lat", location.getLatitude());
            //        intent.putExtra("lon", location.getLongitude());
            //        LocalBroadcastManager.getInstance(ctx).sendBroadcast(intent);
            // TODO Do something.
            //  insertLocation(location);
            //txtLocationRequest.setText(location.getLatitude() + "," + location.getLongitude());
        } else {
            Log.e(TAG, "LOCATION IS NOT VERY ACCURATE");
        }
    }
}
