package com.armut.armuthv.services;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.armut.armuthv.ArmutHVApp;
import com.armut.armuthv.R;
import com.armut.armuthv.busevents.ApiErrorEvent;
import com.armut.armuthv.busevents.MiscEvents;
import com.armut.armuthv.objects.City;
import com.armut.armuthv.objects.Coordinate;
import com.armut.armuthv.objects.District;
import com.armut.armuthv.objects.State;
import com.armut.armuthv.utils.ArmutUtils;
import com.armut.armuthv.utils.Constants;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by oguzemreozcan on 29/07/16.
 */
public class LocationService {

    private final ArmutHVApp app;
    private Bus mBus;
    private final String TAG = "LOCATION_SERVICE";
    private boolean isDialogShowing = false;
    private LocationRestApi.GetStatesRestApi getStatesRestApi;
    private LocationRestApi.GetCitiesRestApi getCitiesRestApi;
    private LocationRestApi.GetDistrictsRestApi getDistrictsRestApi;
    private LocationRestApi.PostCoordinateRestApi postCoordinateRestApi;
    private LocationRestApi.PostCoordinatesRestApi postCoordinatesRestApi;


    public LocationService(ArmutHVApp app, LocationRestApi.GetStatesRestApi getStatesRestApi, LocationRestApi.GetCitiesRestApi getCitiesRestApi,
                           LocationRestApi.GetDistrictsRestApi getDistrictsRestApi, LocationRestApi.PostCoordinateRestApi postCoordinateRestApi, LocationRestApi.PostCoordinatesRestApi postCoordinatesRestApi) {
        this.app = app;
        mBus = app.getBus();
        this.getStatesRestApi = getStatesRestApi;
        this.getCitiesRestApi = getCitiesRestApi;
        this.getDistrictsRestApi = getDistrictsRestApi;
        this.postCoordinateRestApi = postCoordinateRestApi;
        this.postCoordinatesRestApi = postCoordinatesRestApi;
    }

    public LocationService(ArmutHVApp app) {
        this.app = app;
    }

    @Subscribe
    public void onLoadStates(final MiscEvents.GetStatesRequest event) {
        String token = app.isTokenPresent(TAG);
        if (token == null) {
            return;
        }
        getStatesRestApi.getStates(token, ArmutUtils.clientInfo).enqueue(new Callback<ArrayList<State>>() {
            @Override
            public void onResponse(Call<ArrayList<State>> call, Response<ArrayList<State>> response) {
                Log.d(TAG, "ON RESPONSE get states: " + response.isSuccessful() + " - responsecode: " + response.code() + " - response:" + response.message());
                if (response.isSuccessful()) {
                    mBus.post(new MiscEvents.GetStatesResponse(response));
                } else {
                    mBus.post(new ApiErrorEvent(response.code(), ArmutUtils.getErrorMessage(response), false));
                }
            }

            @Override
            public void onFailure(Call<ArrayList<State>> call, Throwable t) {
                Log.d(TAG, "ON RESPONSE get states: " + t.getMessage());
                mBus.post(new ApiErrorEvent());
            }
        });
    }

    @Subscribe
    public void onLoadCities(final MiscEvents.GetCitiesRequest event) {
        String token = app.isTokenPresent(TAG);
        if (token == null) {
            return;
        }
        getCitiesRestApi.getCities(token, ArmutUtils.clientInfo, event.getStateId()).enqueue(new Callback<ArrayList<City>>() {
            @Override
            public void onResponse(Call<ArrayList<City>> call, Response<ArrayList<City>> response) {
                Log.d(TAG, "ON RESPONSE get cities: " + response.isSuccessful() + " - responsecode: " + response.code() + " - response:" + response.message() + " - Url: " + call.request().url());
                if (response.isSuccessful()) {
                    mBus.post(new MiscEvents.GetCitiesResponse(response, event.getStateId()));
                } else {
                    mBus.post(new ApiErrorEvent(response.code(), ArmutUtils.getErrorMessage(response), false));
                }
            }

            @Override
            public void onFailure(Call<ArrayList<City>> call, Throwable t) {
                Log.d(TAG, "ON RESPONSE get cities: " + t.getMessage());
                mBus.post(new ApiErrorEvent());
            }
        });
    }

    @Subscribe
    public void onLoadDistricts(final MiscEvents.GetDistrictsRequest event) {
        String token = app.isTokenPresent(TAG);
        if (token == null) {
            return;
        }
        getDistrictsRestApi.getDistricts(token, ArmutUtils.clientInfo, event.getCityId()).enqueue(new Callback<ArrayList<District>>() {
            @Override
            public void onResponse(Call<ArrayList<District>> call, Response<ArrayList<District>> response) {
                Log.d(TAG, "ON RESPONSE get districts: " + response.isSuccessful() + " - responsecode: " + response.code() + " - response:" + response.message() + " - Url: " + call.request().url());
                if (response.isSuccessful()) {
                    mBus.post(new MiscEvents.GetDistrictsResponse(response, event.getCityId()));
                } else {
                    mBus.post(new ApiErrorEvent(response.code(), ArmutUtils.getErrorMessage(response), false));
                }
            }

            @Override
            public void onFailure(Call<ArrayList<District>> call, Throwable t) {
                Log.d(TAG, "ON RESPONSE get cities: " + t.getMessage());
                mBus.post(new ApiErrorEvent());
            }
        });
    }

    @Subscribe
    public void onCoordinatePosted(final MiscEvents.PostCoordinateRequest event) {
        String token = app.isTokenPresent(TAG);
        if (token == null) {
            return;
        }
        postCoordinateRestApi.postCoordinate(token, ArmutUtils.clientInfo, event.getCoordinate()).enqueue(new Callback<Coordinate>() {
            @Override
            public void onResponse(Call<Coordinate> call, Response<Coordinate> response) {
                Log.d(TAG, "ON RESPONSE post coordinate: " + response.isSuccessful() + " - responsecode: " + response.code() + " - response:" + response.message() + " - Url: " + call.request().url());
                if (response.isSuccessful()) {
                    mBus.post(new MiscEvents.PostCoordinateResponse(response));
                }
                //else {
                   // mBus.post(new ApiErrorEvent(response.code(), ArmutUtils.getErrorMessage(response), false));
                //}
            }

            @Override
            public void onFailure(Call<Coordinate> call, Throwable t) {
                Log.d(TAG, "ON RESPONSE failed postCoordinate: " + t.getMessage());
               // mBus.post(new ApiErrorEvent());
            }
        });
    }

    @Subscribe
    public void onCoordinatesPosted(final MiscEvents.PostCoordinatesRequest event) {
        String token = app.isTokenPresent(TAG);
        if (token == null) {
            return;
        }
        postCoordinatesRestApi.postCoordinates(token, ArmutUtils.clientInfo, event.getCoordinates()).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d(TAG, "ON RESPONSE post coordinates: " + response.isSuccessful() + " - responsecode: " + response.code() + " - response:" + response.message() + " - Url: " + call.request().url());
                if (response.isSuccessful()) {
                    mBus.post(new MiscEvents.PostCoordinatesResponse(response));
                } else {
                    mBus.post(new ApiErrorEvent(response.code(), ArmutUtils.getErrorMessage(response), false));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(TAG, "ON RESPONSE failed postCoordinates: " + t.getMessage());
                mBus.post(new ApiErrorEvent());
            }
        });
    }


    //TODO ADD SILENT LOCATION GETTER (put boolean variable to disable all popups if location is available then just send it as a message)
    public double[] getLatLong(Context context, Activity parentActivity) throws SecurityException {
        double[] latLong = new double[2];
        if (ArmutUtils.isMarshmallow()) {
//            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
//                    && ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//                app.isLocationPremissionGranted = true;
//                Log.d(TAG, "LOCATION PERMISSION GRANTED");
//            } else {
//                Log.d(TAG, "LOCATION PERMISSION istiyoruzzzzzzz");
//                if (parentActivity != null) {
//                    String message = "Lokasyonunu yollayabilmemiz için telefon ayarlarından buna izin vermen gerekli.";
//                    if (ActivityCompat.shouldShowRequestPermissionRationale(parentActivity, Manifest.permission.ACCESS_FINE_LOCATION)) {
//                        app.showMessage(parentActivity,message, Toast.LENGTH_LONG);
//                    } else {
//                        app.showMessage(parentActivity,"Lokasyonunu otomatik olarak alabilmemiz için telefon ayarlarından buna izin vermen gerekli.", Toast.LENGTH_LONG);
//                    }
//                    ActivityCompat.requestPermissions(parentActivity,
//                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
//                            Constants.PERMISSIONS_REQUEST_LOCATION);
//                    return null;
//                }
//            }
            if (!app.requestLocationPermission(parentActivity)) {
                return null;
            }
        } else {
            Log.d(TAG, "MARSHMALLOW DEGIL");
        }

        LocationManager locationManager = (LocationManager) context.
                getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        //criteria.setAccuracy(Criteria.ACCURACY_FINE);
        try {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 900000, 500, locationListener);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 900000, 500, locationListener);
        }
//        ArrayList<String> list = new ArrayList<>(locationManager.getProviders(true));
//        for (String name : list) {
//            Log.e(TAG, "PROVIDER - LOCATION SERVICE : " + name + " - Enabled: " + locationManager.isProviderEnabled(name));
//        }
        String bestProvider = locationManager.getBestProvider(criteria, false);
        Log.e(TAG, "BEST PROVIDER - LOCATION SERVICE : " + bestProvider);
        try {
            Location location = locationManager.getLastKnownLocation(bestProvider);
            latLong[0] = location.getLatitude();
            latLong[1] = location.getLongitude();
        } catch (Exception e) {
            e.printStackTrace();
            //TODO
            openLocationDialog((Activity) context);
            return null;
//            latLong[0] = -1;
//            latLong[1] = -1;
        }
        return latLong;
    }

    private final LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            double lon = location.getLongitude();
            double lat = location.getLatitude();
            Log.e(TAG, "LOCATION location listener provides that: LAT: " + lat + " - Lon: " + lon);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }
    };

    public void openLocationDialog(final Activity activity) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                showLocationDialog(activity.getString(R.string.use_location), activity.getString(R.string.location_info_not_present),
                        activity.getString(R.string.ok), activity.getString(R.string.close), activity);
            }
        }, 50);
    }

    private void showLocationDialog(String title, String message, String posButton, String negButton, final Activity activity) {
        if (!isDialogShowing) {
            final AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
            alertDialog.setTitle(title);
            alertDialog.setMessage(message);
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, posButton, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent dialogIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    activity.startActivityForResult(dialogIntent, Constants.LOCATION_AVAILABLE);
                }
            });

            DialogInterface.OnClickListener cl = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(final DialogInterface dialog, int which) {
                    Log.d(TAG, "DISMISS");
                    dialog.dismiss();
                }
            };
            DialogInterface.OnDismissListener onDismissListener = new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    isDialogShowing = false;
                }
            };
            alertDialog.setOnDismissListener(onDismissListener);
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, negButton, cl);
            alertDialog.setCancelable(true);
            alertDialog.setCanceledOnTouchOutside(true);
            try {
                alertDialog.show();
                isDialogShowing = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
