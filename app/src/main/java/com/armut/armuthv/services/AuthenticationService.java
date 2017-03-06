package com.armut.armuthv.services;

import android.util.Log;

import com.armut.armuthv.busevents.ApiErrorEvent;
import com.armut.armuthv.busevents.AuthRequest;
import com.armut.armuthv.busevents.AuthResponse;
import com.armut.armuthv.busevents.LiveAuthRequest;
import com.armut.armuthv.utils.ArmutUtils;
import com.google.gson.annotations.SerializedName;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by oguzemreozcan on 15/06/16.
 */
public class AuthenticationService {

    private final Bus mBus;
    private final AuthRestApi.AuthenticationRestApi authRestApi;
    private final AuthRestApi.LiveAuthenticationRestApi liveAuthenticationRestApi;
    private final String TAG = "AuthService";

    public AuthenticationService(Bus mBus, AuthRestApi.AuthenticationRestApi authRestApi, AuthRestApi.LiveAuthenticationRestApi liveAuthRestApi){
        this.mBus = mBus;
        this.authRestApi = authRestApi;
        this.liveAuthenticationRestApi = liveAuthRestApi;
    }

    @Subscribe
    public void getAuthToken(final AuthRequest event){
        authRestApi.getAuthToken(event.getUserName(), event.getPassword(), event.getImpersonate(), event.getGrantType(), event.getClientId()).enqueue(new Callback<AuthObject>() {
            @Override
            public void onResponse(Call<AuthObject> call, Response<AuthObject> response) {
                Log.d(TAG, "ON RESPONSE auth: " + response.isSuccessful() + " - responsecode: " + response.code() + " - response:" + response.message());
                if (response.isSuccessful()) {
                    mBus.post(new AuthResponse(response, event.getImpersonate()));
                }
                else {
                    mBus.post(new ApiErrorEvent(response.code(), ArmutUtils.getAuthErrorMessage(response), false));
                    mBus.post(new AuthResponse(null, null));
                }
            }

            @Override
            public void onFailure(Call<AuthObject> call, Throwable t) {
                Log.d(TAG, "ON FAILURE: " + t.getMessage());
                t.printStackTrace();
                mBus.post(new AuthResponse(null, null));
                mBus.post(new ApiErrorEvent());
            }
        });
    }

    @Subscribe
    public void getLiveAuthToken(final LiveAuthRequest event){
        liveAuthenticationRestApi.getAuthToken(event.getUserName(), event.getPassword(), event.getGrantType(), event.getClientId()).enqueue(new Callback<AuthObject>() {
            @Override
            public void onResponse(Call<AuthObject> call, Response<AuthObject> response) {
                Log.d(TAG, "ON RESPONSE auth: " + response.isSuccessful() + " - responsecode: " + response.code() + " - response:" + response.message());
                if (response.isSuccessful()) {
                    mBus.post(new AuthResponse(response, event.getUserName()));
                }
                else {
                    if(response.code() == 400){
                        mBus.post(new ApiErrorEvent(response.code(), "Kullanıcı adı veya şifre yanlış", false));
                    }else if(response.code() == 500){
                        mBus.post(new ApiErrorEvent(response.code(), "Üstüste hatalı şifre girdiniz, lütfen daha sonra tekrar deneyin", false));
                    }
                    mBus.post(new AuthResponse(null, null));
                }
            }

            @Override
            public void onFailure(Call<AuthObject> call, Throwable t) {
                Log.d(TAG, "ON FAILURE: " + t.getMessage());
                t.printStackTrace();
                mBus.post(new ApiErrorEvent());
            }
        });
    }

    public class AuthObject{
        @SerializedName("access_token")
        private String accessToken;
        @SerializedName("token_type")
        private String tokenType;
        @SerializedName("expires_in")
        private long expiresIn;

        public String getAccessToken() {
            return accessToken;
        }

        public String getTokenType() {
            return tokenType;
        }

        public long getExpiresIn() {
            return expiresIn;
        }
    }
}
