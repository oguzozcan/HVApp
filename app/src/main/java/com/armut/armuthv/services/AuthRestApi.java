package com.armut.armuthv.services;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * Created by oguzemreozcan on 15/06/16.
 */
public class AuthRestApi {

    public interface AuthenticationRestApi{
        @FormUrlEncoded
        @POST("oauth2/token")
        Call<AuthenticationService.AuthObject> getAuthToken(@Field("username") String username, @Field("password") String password, @Header("Impersonate-User") String impersonate,
                                                            @Field("grant_type") String grantType, @Field("client_id") String clientId);
    }

    public interface LiveAuthenticationRestApi{
        @FormUrlEncoded
        @POST("oauth2/token")
        Call<AuthenticationService.AuthObject> getAuthToken(@Field("username") String username, @Field("password") String password,
                                                            @Field("grant_type") String grantType, @Field("client_id") String clientId);
    }
}
