package com.armut.armuthv.services;

import com.armut.armuthv.objects.Address;
import com.armut.armuthv.objects.CustomerReview;
import com.armut.armuthv.objects.Profile;
import com.armut.armuthv.objects.Review;
import com.armut.armuthv.objects.User;
import com.armut.armuthv.objects.UserCalendarItem;
import com.armut.armuthv.objects.UserInfo;
import com.armut.armuthv.utils.Constants;

import java.util.ArrayList;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by oguzemreozcan on 05/08/16.
 */
public class UserRestApi {

    public interface GetUserRestApi{
        @GET(Constants.USER_DETAILS_POSTFIX)
        Call<User> getUser(@Header("Authorization") String token, @Header("client_info") String clientInfo);
    }

    public interface GetUserProfilesRestApi{
        @GET(Constants.PROFILES_POSTFIX)
        Call<ArrayList<Profile>> getProfile(@Header("Authorization") String token, @Header("client_info") String clientInfo);
    }

    public interface GetUserCalendar{
        @GET(Constants.CALENDAR_POSTFIX)
        Call<ArrayList<UserCalendarItem>> getUserCalendar(@Header("Authorization") String token, @Header("client_info") String clientInfo);
    }

    public interface PatchUser{
        @PATCH(Constants.USERS_POSTFIX)
        Call<ResponseBody> updateUser(@Header("Authorization") String token, @Header("client_info") String clientInfo, @Body UserInfo user);
    }

    public interface PatchUserCalendar{
        @PATCH(Constants.CALENDAR_POSTFIX)
        Call<ResponseBody> updateUserCalendar(@Header("Authorization") String token, @Header("client_info") String clientInfo, @Body ArrayList<UserCalendarItem> item);
    }

    public interface PostUserPhoto{
        @POST(Constants.PHOTO_UPLOAD_POSTFIX)
        Call<ResponseBody> uploadProfilePhoto(@Header("Authorization") String token, @Header("client_info") String clientInfo, @Header("Content-Type") String contentType, @Body RequestBody encodedImage);
    }

    public interface PatchUserAddress{
        @PATCH(Constants.USER_ADDRESS_POSTFIX)
        Call<ResponseBody> updateUserAddress(@Header("Authorization") String token, @Header("client_info") String clientInfo, @Body Address item);
    }

    public interface PatchUserPassword{
        @PATCH(Constants.USER_ACCOUNT_POSTFIX)
        Call<ResponseBody> updateUserPassword(@Header("Authorization") String token, @Header("client_info") String clientInfo, @Body User.ModifyPassword item);
    }

    public interface GetRatingsApi{
        //, @Query("from_user_id") String fromUserId, @Query("job_id") String jobId
        @GET(Constants.RATINGS_POSTFIX)
        Call<ArrayList<Review>> getRatings(@Header("Authorization") String token, @Header("client_info") String clientInfo, @Query("for_user_id") String forUserId);
    }

    public interface GetAssignedPhoneNumber{
        @GET(Constants.USER_ASSIGN_PHONE_NUMBER_POSTFIX)
        Call<ResponseBody> getAssignedPhoneNumber(@Header("Authorization") String token, @Header("client_info") String clientInfo, @Query("user_id") String userId, @Query("job_id") long jobId);
    }

    public interface PostUserAppReview{
        @POST(Constants.USER_REVIEWS_POSTFIX)
        Call<ResponseBody> postUserAppReview(@Header("Authorization") String token, @Header("client_info") String clientInfo, @Body CustomerReview review);
    }
}
