package com.armut.armuthv.services;

import com.armut.armuthv.objects.DeviceToken;
import com.armut.armuthv.objects.Message;
import com.armut.armuthv.utils.Constants;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by oguzemreozcan on 25/07/16.
 */
public class MessagesRestApi {
    public interface JobMessagesRestApi{
        @GET(Constants.MESSAGES_POSTFIX)
        Call<ArrayList<Message>> getJobMessages(@Header("Authorization") String token, @Header("client_info") String clientInfo, @Query("job_quotes_id") int jobQuotesId);
    }

    public interface PostJobMessagesRestApi{
        @POST(Constants.MESSAGES_POSTFIX)
        Call<Message> onMessageSent(@Header("Authorization") String token, @Header("client_info") String clientInfo, @Body Message messageJsonBody);
    }

    public interface PostDeviceTokenRestApi{
        @POST(Constants.DEVICES_POSTFIX)
        Call<DeviceToken> onDeviceTokenSent(@Header("Authorization") String token, @Header("client_info") String clientInfo, @Body DeviceToken deviceToken);
    }

    public interface DeleteDeviceTokenRestApi{
        @DELETE(Constants.DEVICES_POSTFIX)
        Call<ResponseBody> onDeviceTokenDeleted(@Header("Authorization") String token, @Header("client_info") String clientInfo, @Query("device_token") String deviceToken, @Query("deviceos_id") int deviceOsId);
    }
}
