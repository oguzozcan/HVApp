package com.armut.armuthv.services;

import android.util.Log;

import com.armut.armuthv.ArmutHVApp;
import com.armut.armuthv.busevents.ApiErrorEvent;
import com.armut.armuthv.busevents.MiscEvents;
import com.armut.armuthv.objects.Profile;
import com.armut.armuthv.objects.Review;
import com.armut.armuthv.objects.User;
import com.armut.armuthv.objects.UserCalendarItem;
import com.armut.armuthv.utils.ArmutUtils;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by oguzemreozcan on 05/08/16.
 */
public class UserService {

    private final Bus mBus;
    private final ArmutHVApp app;
    private final UserRestApi.GetUserRestApi userRestApi;
    private final UserRestApi.GetUserProfilesRestApi userProfilesRestApi;
    private final UserRestApi.GetUserCalendar getCalendarRestApi;
    private final UserRestApi.PatchUserCalendar patchCalendarRestApi;
    private final UserRestApi.PostUserPhoto postUserPhoto;
    private final UserRestApi.PatchUser patchUserRestApi;
    private final UserRestApi.PatchUserAddress patchUserAddress;
    private final UserRestApi.GetRatingsApi getRatingsApi;
    private final UserRestApi.GetAssignedPhoneNumber getAssignedPhoneNumber;
    private final UserRestApi.PostUserAppReview postUserAppReview;
    private final UserRestApi.PatchUserPassword patchUserPassword;
    private final String TAG = "UserService";

    public UserService(ArmutHVApp app, UserRestApi.GetUserRestApi userRestApi, UserRestApi.GetUserProfilesRestApi userProfilesRestApi, UserRestApi.GetUserCalendar getCalendarRestApi,
                       UserRestApi.PatchUserCalendar patchCalendarRestApi, UserRestApi.PostUserPhoto postUserPhoto, UserRestApi.PatchUser patchUser, UserRestApi.PatchUserAddress patchUserAddress,
                       UserRestApi.GetRatingsApi getRatingsApi, UserRestApi.GetAssignedPhoneNumber getAssignedPhoneNumber, UserRestApi.PatchUserPassword patchUserPassword, UserRestApi.PostUserAppReview postUserAppReview){
        this.app = app;
        mBus = app.getBus();
        this.userRestApi = userRestApi;
        this.userProfilesRestApi = userProfilesRestApi;
        this.getCalendarRestApi = getCalendarRestApi;
        this.patchCalendarRestApi = patchCalendarRestApi;
        this.postUserPhoto = postUserPhoto;
        this.patchUserRestApi = patchUser;
        this.patchUserAddress = patchUserAddress;
        this.getRatingsApi = getRatingsApi;
        this.getAssignedPhoneNumber = getAssignedPhoneNumber;
        this.patchUserPassword = patchUserPassword;
        this.postUserAppReview = postUserAppReview;
    }

    @Subscribe
    public void onLoadUser(final MiscEvents.UserRequest event){
        String token = app.isTokenPresent(TAG);
        if(token == null){
            mBus.post(new MiscEvents.UserResponse(null));
            return;
        }
        userRestApi.getUser(token, ArmutUtils.clientInfo).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                Log.d(TAG, "ON RESPONSE user : " + response.isSuccessful() + " - responsecode: " + response.code() + " - response:" + response.message());
                if (response.isSuccessful()) {
                    mBus.post(new MiscEvents.UserResponse(response));
                }
                else {
                    mBus.post(new ApiErrorEvent(response.code(), ArmutUtils.getErrorMessage(response), false));
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.d(TAG, "ON RESPONSE get user: " + t.getMessage());
                mBus.post(new MiscEvents.UserResponse(null));
                mBus.post(new ApiErrorEvent());
            }
        });
    }

    @Subscribe
    public void onLoadRatings(final MiscEvents.GetRatingsRequest event){
        String token = app.isTokenPresent(TAG);
        if(token == null){
            return;
        }
        getRatingsApi.getRatings(token, ArmutUtils.clientInfo, event.getForUserId()).enqueue(new Callback<ArrayList<Review>>() {
            @Override
            public void onResponse(Call<ArrayList<Review>> call, Response<ArrayList<Review>> response) {
                Log.d(TAG, "ON RESPONSE ratings : " + response.isSuccessful() + " - responsecode: " + response.code() + " - response:" + response.message());
                if (response.isSuccessful()) {
                    mBus.post(new MiscEvents.GetRatingsResponse(response));
                } else {
                    mBus.post(new ApiErrorEvent(response.code(), ArmutUtils.getErrorMessage(response), false));
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Review>> call, Throwable t) {
                Log.d(TAG, "ON RESPONSE failed ratings: " + t.getMessage() + " - t trace: "  + "   - t.code: " + t.toString() + "url: " + call.request().url());
                mBus.post(new ApiErrorEvent());
                t.getStackTrace();
            }
        });
    }

    @Subscribe
    public void onLoadProfile(final MiscEvents.ProfileRequest event){
        String token = app.isTokenPresent(TAG);
        if(token == null){
            return;
        }
        userProfilesRestApi.getProfile(token, ArmutUtils.clientInfo).enqueue(new Callback<ArrayList<Profile>>() {
            @Override
            public void onResponse(Call<ArrayList<Profile>> call, Response<ArrayList<Profile>> response) {
                Log.d(TAG, "ON RESPONSE profile : " + response.isSuccessful() + " - responsecode: " + response.code() + " - response:" + response.message());
                if (response.isSuccessful()) {
                    mBus.post(new MiscEvents.ProfileResponse(response));
                } else {
                    mBus.post(new ApiErrorEvent(response.code(), ArmutUtils.getErrorMessage(response), false));
                    //Not found user
                    if(response.code() == 404){
                        mBus.post(new MiscEvents.ProfileResponse(response));
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Profile>> call, Throwable t) {
                Log.d(TAG, "ON RESPONSE onloadprofile: " + t.getMessage() + " - t trace: " + t.getStackTrace() + "   - t.code: " + t.toString() + "url: " + call.request().url());
                mBus.post(new ApiErrorEvent());
            }
        });
    }

    @Subscribe
    public void onLoadCalendarData(final MiscEvents.GetCalendarRequest event){
        String token = app.isTokenPresent(TAG);
        if(token == null){
            return;
        }
        getCalendarRestApi.getUserCalendar(token, ArmutUtils.clientInfo).enqueue(new Callback<ArrayList<UserCalendarItem>>() {
            @Override
            public void onResponse(Call<ArrayList<UserCalendarItem>> call, Response<ArrayList<UserCalendarItem>> response) {
                Log.d(TAG, "ON RESPONSE get calendar : " + response.isSuccessful() + " - responsecode: " + response.code() + " - response:" + response.message() + "url: " + call.request().url());
                if (response.isSuccessful()) {
                    mBus.post(new MiscEvents.GetCalendarResponse(response));
                } else {
                    mBus.post(new ApiErrorEvent(response.code(), ArmutUtils.getErrorMessage(response), false));
                }
            }

            @Override
            public void onFailure(Call<ArrayList<UserCalendarItem>> call, Throwable t) {
                Log.d(TAG, "ON RESPONSE getCalendar: " + t.getMessage() + " - t trace: " + t.getStackTrace() + "   - t.code: " + t.toString() + "url: " + call.request().url());
                mBus.post(new ApiErrorEvent());
            }
        });
    }

    @Subscribe
    public void onProfilePhotoUploaded(final MiscEvents.PostProfilePhotoRequest event){
        String token = app.isTokenPresent(TAG);
        if(token == null){
            return;
        }
        byte[] encodedImage = event.getImageEncoded();
        if(encodedImage != null) {
            RequestBody body = RequestBody.create(MediaType.parse("image/jpeg"), encodedImage);
            postUserPhoto.uploadProfilePhoto(token, ArmutUtils.clientInfo,"image/jpeg", body).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    Log.d(TAG, "ON RESPONSE upload profile photo : " + response.isSuccessful() + " - responsecode: " + response.code() + " - response:" + response.message() + "url: " + call.request().url() + "  - response.body : " + response.body().toString());
                    if (response.isSuccessful()) {
                        try {
                            String txtResponse = response.body().string();
                            Log.d(TAG, "RESPONSE : " + txtResponse);
                            mBus.post(new MiscEvents.PostProfilePhotoResponse(txtResponse));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    } else {
                        mBus.post(new ApiErrorEvent(response.code(), ArmutUtils.getErrorMessage(response), false));
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.d(TAG, "ON RESPONSE upload photo failed: " + t.getMessage() + " - t trace: " + t.getStackTrace() + "   - t.code: " + t.toString() + "url: " + call.request().url());
                    mBus.post(new ApiErrorEvent());
                }
            });
        }
    }

    @Subscribe
    public void onCalendarItemUpdated(final MiscEvents.PatchCalendarRequest event){
        String token = app.isTokenPresent(TAG);
        if(token == null){
            return;
        }
        patchCalendarRestApi.updateUserCalendar(token, ArmutUtils.clientInfo, event.getCalendarItem()).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d(TAG, "ON RESPONSE update calendar : " + response.isSuccessful() + " - responsecode: " + response.code() + " - response:" + response.message() + "url: " + call.request().url());
                if (response.isSuccessful()) {
                    mBus.post(new MiscEvents.PatchCalendarResponse(response));
                } else {
                    mBus.post(new ApiErrorEvent(response.code(), ArmutUtils.getErrorMessage(response), false));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(TAG, "ON RESPONSE updateCalendar: " + t.getMessage() + " - t trace: " + t.getStackTrace() + "   - t.code: " + t.toString() + "url: " + call.request().url() + " - ");
                mBus.post(new ApiErrorEvent());
            }
        });
    }

    @Subscribe
    public void onCalendarItemUpdated(final MiscEvents.PatchAddressRequest event){
        String token = app.isTokenPresent(TAG);
        if(token == null){
            return;
        }
        patchUserAddress.updateUserAddress(token, ArmutUtils.clientInfo, event.getAddress()).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d(TAG, "ON RESPONSE update Address : " + response.isSuccessful() + " - responsecode: " + response.code() + " - response:" + response.message() + "url: " + call.request().url());
                if (response.isSuccessful()) {
                    mBus.post(new MiscEvents.PatchAddressResponse(response));
                } else {
                    mBus.post(new ApiErrorEvent(response.code(), ArmutUtils.getErrorMessage(response), false));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(TAG, "ON RESPONSE failed updateAddress: " + t.getMessage() + " - t trace: " + t.getStackTrace() + "   - t.code: " + t.toString() + "url: " + call.request().url());
                mBus.post(new ApiErrorEvent());
            }
        });
    }

    @Subscribe
    public void onUserUpdated(final MiscEvents.PatchUserRequest event){
        String token = app.isTokenPresent(TAG);
        if(token == null){
            return;
        }
        patchUserRestApi.updateUser(token, ArmutUtils.clientInfo, event.getUser()).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d(TAG, "ON RESPONSE update user : " + response.isSuccessful() + " - responsecode: " + response.code() + " - response:" + response.message() + "url: " + call.request().url());
                if (response.isSuccessful()) {
                    mBus.post(new MiscEvents.PatchUserResponse(response));
                } else {
                    mBus.post(new ApiErrorEvent(response.code(), ArmutUtils.getErrorMessage(response), false));
                    mBus.post(new MiscEvents.PatchUserResponse(null));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(TAG, "ON RESPONSE user: " + t.getMessage() + " - t trace: " + t.getStackTrace() + "   - t.code: " + t.toString() + "url: " + call.request().url());
                mBus.post(new ApiErrorEvent());
            }
        });
    }

    @Subscribe
    public void onPhoneNumberAssigned(final MiscEvents.GetAssignedPhoneNumberRequest event){
        String token = app.isTokenPresent(TAG);
        if(token == null){
            return;
        }
        getAssignedPhoneNumber.getAssignedPhoneNumber(token, ArmutUtils.clientInfo, event.getUserId(), event.getJobId()).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d(TAG, "ON RESPONSE assign phone number to user : " + response.isSuccessful() + " - responsecode: " + response.code() + " - response:" + response.message() + "url: " + call.request().url());
                if (response.isSuccessful()) {
                    mBus.post(new MiscEvents.GetAssignedPhoneNumberResponse(response));
                } else {
                    mBus.post(new ApiErrorEvent(response.code(), ArmutUtils.getErrorMessage(response), false));
                    mBus.post(new MiscEvents.GetAssignedPhoneNumberResponse(null));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(TAG, "ON RESPONSE user: " + t.getMessage() + " - t trace: " + t.getStackTrace() + "   - t.code: " + t.toString() + "url: " + call.request().url());
                mBus.post(new ApiErrorEvent());
            }
        });
    }

    @Subscribe
    public void onUserPasswordUpdated(final MiscEvents.PatchUserPasswordRequest event){
        String token = app.isTokenPresent(TAG);
        if(token == null){
            return;
        }
        patchUserPassword.updateUserPassword(token, ArmutUtils.clientInfo, event.getModifyPassObject()).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d(TAG, "ON RESPONSE update user password : " + response.isSuccessful() + " - responsecode: " + response.code() + " - response:" + response.message() + "url: " + call.request().url());
                if (response.isSuccessful()) {
                    mBus.post(new MiscEvents.PatchUserPasswordResponse(response));
                } else {
                    mBus.post(new MiscEvents.PatchUserPasswordResponse(null));
                    mBus.post(new ApiErrorEvent(response.code(), ArmutUtils.getErrorMessage(response), false));

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(TAG, "ON RESPONSE user update password failed: " + t.getMessage() + " - t trace: " + t.getStackTrace() + "   - t.code: " + t.toString() + "url: " + call.request().url());
                mBus.post(new MiscEvents.PatchUserPasswordResponse(null));
                mBus.post(new ApiErrorEvent());
            }
        });
    }

    @Subscribe
    public void onUserAppReviewPosted(final MiscEvents.PostUserAppReviewRequest event){
        String token = app.isTokenPresent(TAG);
        if(token == null){
            return;
        }
        postUserAppReview.postUserAppReview(token, ArmutUtils.clientInfo, event.getReview()).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d(TAG, "ON RESPONSE post user response : " + response.isSuccessful() + " - responsecode: " + response.code() + " - response:" + response.message() + "url: " + call.request().url());
                if (response.isSuccessful()) {
                    mBus.post(new MiscEvents.PostUserAppReviewResponse(response));
                } else {
                    mBus.post(new ApiErrorEvent(response.code(), ArmutUtils.getErrorMessage(response), false));
                    mBus.post(new MiscEvents.PostUserAppReviewResponse(null));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(TAG, "ON RESPONSE failed user app review: " + t.getMessage() + " - t trace: " + t.getStackTrace() + "   - t.code: " + t.toString() + "url: " + call.request().url());
                mBus.post(new ApiErrorEvent());
                mBus.post(new MiscEvents.PostUserAppReviewResponse(null));
            }
        });
    }
}
