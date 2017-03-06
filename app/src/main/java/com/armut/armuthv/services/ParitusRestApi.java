package com.armut.armuthv.services;

import com.armut.armuthv.objects.ParitusVerifyObject;
import com.armut.armuthv.utils.Constants;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by oguzemreozcan on 11/01/17.
 */

public class ParitusRestApi {
    public interface VerifyAddress{
        @GET(Constants.PARITUS_VERIFY)
        Call<ParitusVerifyObject> verifyAddress(@Query("apikey") String apikey, @Query("address")String address);
    }
}
