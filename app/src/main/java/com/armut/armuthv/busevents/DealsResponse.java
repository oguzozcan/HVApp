package com.armut.armuthv.busevents;

import com.armut.armuthv.objects.Job;

import java.util.ArrayList;

import retrofit2.Response;

/**
 * Created by oguzemreozcan on 25/05/16.
 */
public class DealsResponse {


    private final Response<ArrayList<Job>> response;

    public DealsResponse(Response<ArrayList<Job>> response) {
        this.response = response;
    }

    public Response<ArrayList<Job>> getResponse() {
        return response;
    }

}
