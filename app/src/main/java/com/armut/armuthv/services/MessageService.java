package com.armut.armuthv.services;

import android.util.Log;

import com.armut.armuthv.ArmutHVApp;
import com.armut.armuthv.busevents.ApiErrorEvent;
import com.armut.armuthv.busevents.MiscEvents;
import com.armut.armuthv.objects.DeviceToken;
import com.armut.armuthv.objects.Message;
import com.armut.armuthv.utils.ArmutUtils;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by oguzemreozcan on 25/07/16.
 */
public class MessageService {

    private final Bus mBus;
    private final ArmutHVApp app;
    private final String TAG = "MessageService";
    private final MessagesRestApi.JobMessagesRestApi messagesRestApi;
    private final MessagesRestApi.PostJobMessagesRestApi postMessagesRestApi;
    private final MessagesRestApi.PostDeviceTokenRestApi postDeviceTokenRestApi;
    private final MessagesRestApi.DeleteDeviceTokenRestApi deleteDeviceTokenRestApi;

    public MessageService(ArmutHVApp app, MessagesRestApi.JobMessagesRestApi messagesRestApi, MessagesRestApi.PostJobMessagesRestApi postMessagesRestApi,
                          MessagesRestApi.PostDeviceTokenRestApi postDeviceTokenRestApi, MessagesRestApi.DeleteDeviceTokenRestApi deleteDeviceTokenRestApi){
        this.app = app;
        mBus = app.getBus();
        this.messagesRestApi = messagesRestApi;
        this.postMessagesRestApi = postMessagesRestApi;
        this.postDeviceTokenRestApi = postDeviceTokenRestApi;
        this.deleteDeviceTokenRestApi = deleteDeviceTokenRestApi;
    }

    @Subscribe
    public void onLoadJobMessages(final MiscEvents.MessagesRequest event){
        String token = app.isTokenPresent(TAG);
        if(token == null){
            return;
        }
        messagesRestApi.getJobMessages(token, ArmutUtils.clientInfo, event.getJobQuotesId()).enqueue(new Callback<ArrayList<Message>>() {
            @Override
            public void onResponse(Call<ArrayList<Message>> call, Response<ArrayList<Message>> response) {
                Log.d(TAG, "ON RESPONSE messages : " + response.isSuccessful() + " - responsecode: " + response.code() + " - response:" + response.message());
                if (response.isSuccessful()) {
                    mBus.post(new MiscEvents.MessagesResponse(response));
                }
                else {
                    mBus.post(new ApiErrorEvent(response.code(), ArmutUtils.getErrorMessage(response), false));
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Message>> call, Throwable t) {
                Log.d(TAG, "ON RESPONSE get job messages: " + t.getMessage());
                mBus.post(new ApiErrorEvent());
            }
        });
    }

    @Subscribe
    public void onDeviceTokenPosted(final MiscEvents.PostDeviceTokenRequest event){
        String token = app.isTokenPresent(TAG);
        if(token == null){
            return;
        }
        postDeviceTokenRestApi.onDeviceTokenSent(token, ArmutUtils.clientInfo, event.getToken()).enqueue(new Callback<DeviceToken>() {
            @Override
            public void onResponse(Call<DeviceToken> call, Response<DeviceToken> response) {
                Log.d(TAG, "ON RESPONSE post DEVICE TOKEN : " + response.isSuccessful() + " - responsecode: " + response.code() + " - response:" + response.message());

                Log.d(TAG, "DEVICE TOKEN: " + event.getToken() );
                if (response.isSuccessful()) {
                    MiscEvents.PostDeviceTokenResponse res = new MiscEvents.PostDeviceTokenResponse(response);
                    res.setDeviceToken(event.getToken());
                    mBus.post(res);
                } else {
                    mBus.post(new ApiErrorEvent(response.code(), ArmutUtils.getErrorMessage(response), false));
                }
            }

            @Override
            public void onFailure(Call<DeviceToken> call, Throwable t) {
                Log.d(TAG, "ON RESPONSE failure post DEVICE TOKEN: " + t.getMessage()   + "   - t.code: " + t.toString());
                t.getStackTrace();
                Log.d(TAG, "ON FAILURE: " + ArmutUtils.bodyToString(call.request().body()));
                mBus.post(new ApiErrorEvent());
            }
        });
    }

    @Subscribe
    public void onDeviceTokenDeleted(final MiscEvents.DeleteDeviceTokenRequest event){
        String token = app.isTokenPresent(TAG);
        if(token == null){
            return;
        }
        deleteDeviceTokenRestApi.onDeviceTokenDeleted(token, ArmutUtils.clientInfo, event.getDeviceToken(), 0 ).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d(TAG, "ON RESPONSE delete DEVICE TOKEN : " + response.isSuccessful() + " - responsecode: " + response.code() + " - response:" + response.message());

                Log.d(TAG, "DEVICE TOKEN: " + event.getDeviceToken() );
                if (response.isSuccessful()) {
                    mBus.post(new MiscEvents.DeleteDeviceTokenResponse(response));
                }
                //else {
                    //mBus.post(new ApiErrorEvent(response.code(), ArmutUtils.getErrorMessage(response), false));
                //}
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(TAG, "ON RESPONSE failure delete DEVICE TOKEN: " + t.getMessage()   + "   - t.code: " + t.toString());
                t.getStackTrace();
                Log.d(TAG, "ON FAILURE: " + ArmutUtils.bodyToString(call.request().body()));
            }
        });
    }

    @Subscribe
    public void onMessagePosted(final MiscEvents.PostMessageRequest event){
        String token = app.isTokenPresent(TAG);
        if(token == null){
            return;
        }
        postMessagesRestApi.onMessageSent(token, ArmutUtils.clientInfo, event.getMessage()).enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, Response<Message> response) {
                Log.d(TAG, "ON RESPONSE post message : " + response.isSuccessful() + " - responsecode: " + response.code() + " - response:" + response.message());
                if (response.isSuccessful()) {
                    mBus.post(new MiscEvents.PostMessageResponse(response, event.getEditTextWeakReference()));
                } else {
                    mBus.post(new ApiErrorEvent(response.code(), ArmutUtils.getErrorMessage(response), false));
                }
            }

            @Override
            public void onFailure(Call<Message> call, Throwable t) {
                Log.d(TAG, "ON RESPONSE failure post message: " + t.getMessage()   + "   - t.code: " + t.toString());
                t.getStackTrace();
                Log.d(TAG, "ON FAILURE: " + ArmutUtils.bodyToString(call.request().body()));
                mBus.post(new ApiErrorEvent());
            }
        });
    }
}
