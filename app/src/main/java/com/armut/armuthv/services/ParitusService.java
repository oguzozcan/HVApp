package com.armut.armuthv.services;

import android.util.Log;

import com.armut.armuthv.ArmutHVApp;
import com.armut.armuthv.busevents.ApiErrorEvent;
import com.armut.armuthv.busevents.MiscEvents;
import com.armut.armuthv.objects.ParitusVerifyObject;
import com.google.gson.Gson;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by oguzemreozcan on 11/01/17.
 */

public class ParitusService {

    private final Bus mBus;
    private final String TAG = "ParitusService";
    private final ParitusRestApi.VerifyAddress verifyAddressRestApi;

    public ParitusService(ArmutHVApp app, ParitusRestApi.VerifyAddress verifyAddressRestApi){
        mBus = app.getBus();
        this.verifyAddressRestApi = verifyAddressRestApi;
    }

    @Subscribe
    public void onVerifyAddress(final MiscEvents.ParitusVerifyAddressRequest event){
        String apiKey = "a294115bb71f6db79fda3e163fa97fc979d05d06";
        verifyAddressRestApi.verifyAddress(apiKey, event.getAddress()).enqueue(new Callback<ParitusVerifyObject>() {

            @Override
            public void onResponse(Call<ParitusVerifyObject> call, Response<ParitusVerifyObject> response) {
                Log.d(TAG, "ON RESPONSE paritus verify address : " + response.isSuccessful() + " - responsecode: " + response.code() + " - response:" + response.message() + " - url : " + call.request().url());
                Gson gson = new Gson();
                Log.d(TAG, "ON RESPONSE paritus raw: " +gson.toJson(response.body()));

                if (response.isSuccessful()) {
                    mBus.post(new MiscEvents.ParitusVerifyAddressResponse(response));
                } else {
                    //mBus.post(new ApiErrorEvent(response.code(), ArmutUtils.getErrorMessage(response), false));
                }
            }

            @Override
            public void onFailure(Call<ParitusVerifyObject> call, Throwable t) {
                Log.d(TAG, "ON RESPONSE get verify paritus failed: " + t.getMessage() + " - call: " + call.request().url());
                mBus.post(new ApiErrorEvent());
            }
        });
    }
}
