package com.armut.armuthv.busevents;

import com.armut.armuthv.services.AuthenticationService;

import retrofit2.Response;

/**
 * Created by oguzemreozcan on 15/06/16.
 */
public class AuthResponse {

    private final Response<AuthenticationService.AuthObject> response;
    private final String userId;

    public AuthResponse(Response<AuthenticationService.AuthObject> response, final String userId) {
        this.response = response;
        this.userId = userId;
    }

    public Response<AuthenticationService.AuthObject> getResponse() {
        return response;
    }

    public String getUserId() {
        return userId;
    }
}
