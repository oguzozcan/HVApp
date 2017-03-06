package com.armut.armuthv.services;

import android.util.Log;

import com.armut.armuthv.ArmutHVApp;
import com.armut.armuthv.busevents.ApiErrorEvent;
import com.armut.armuthv.busevents.JobEvents;
import com.armut.armuthv.objects.CommissionRate;
import com.armut.armuthv.objects.Job;
import com.armut.armuthv.objects.JobDetails;
import com.armut.armuthv.objects.OpportunityCancelReason;
import com.armut.armuthv.objects.QuoteLead;
import com.armut.armuthv.utils.ArmutUtils;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by oguzemreozcan on 17/05/16.
 */
public class JobService {

    private final Bus mBus;
    private final ArmutHVApp app;
    private final String TAG = "JobService";
    private final JobRestApi.JobOpportunitiesRestApi jobOpportunitiesRestApi;
    private final JobRestApi.JobQuotesRestApi jobQuotesRestApi;
    private final JobRestApi.JobDealsRestApi jobDealsRestApi;
    private final JobRestApi.JobDetailsRestApi jobDetailsRestApi;
    private final JobRestApi.PostQuoteRestApi postQuoteRestApi;
    private final JobRestApi.LeadPriceRestApi leadPriceRestApi;
    private final JobRestApi.JobOpportunityRejectApi postOpportunityRejectApi;
    private final JobRestApi.RejectReasonsApi rejectReasonsApi;
    private final JobRestApi.JobQuoteCommissionApi commissionApi;

    public JobService(ArmutHVApp app, JobRestApi.JobOpportunitiesRestApi jobOpportunitiesRestApi, JobRestApi.JobQuotesRestApi jobQuotesRestApi,
                      JobRestApi.JobDealsRestApi jobDealsRestApi, JobRestApi.JobDetailsRestApi jobDetailsRestApi, JobRestApi.PostQuoteRestApi postQuoteRestApi,
                        JobRestApi.LeadPriceRestApi leadPriceRestApi, JobRestApi.JobOpportunityRejectApi opportunityRejectApi, JobRestApi.RejectReasonsApi rejectReasonsApi,
                        JobRestApi.JobQuoteCommissionApi commissionApi){
        this.app = app;
        mBus = app.getBus();
        this.jobOpportunitiesRestApi = jobOpportunitiesRestApi;
        this.jobQuotesRestApi = jobQuotesRestApi;
        this.jobDealsRestApi = jobDealsRestApi;
        this.jobDetailsRestApi = jobDetailsRestApi;
        this.postQuoteRestApi = postQuoteRestApi;
        this.leadPriceRestApi = leadPriceRestApi;
        this.postOpportunityRejectApi = opportunityRejectApi;
        this.rejectReasonsApi = rejectReasonsApi;
        this.commissionApi = commissionApi;
    }

    @Subscribe
    public void onLoadJobQuotes(final JobEvents.QuotesRequest event){
        //TODO move token and clientINfo out of the events to services.
        //String token = app.getDataSaver().getString(Constants.ACCESS_TOKEN_KEY);
        jobQuotesRestApi.getJobQuotes(event.getToken(), ArmutUtils.clientInfo).enqueue(new Callback<ArrayList<Job>>() {
            @Override
            public void onResponse(Call<ArrayList<Job>> call, Response<ArrayList<Job>> response) {
                Log.d(TAG, "ON RESPONSE quotes: " + response.isSuccessful() + " - responsecode: " + response.code() + " - response:" + response.message());
                if (response.isSuccessful()) {
                    mBus.post(new JobEvents.QuotesResponse(response));
                }
                else {
                    mBus.post(new ApiErrorEvent(response.code(), ArmutUtils.getErrorMessage(response), false));
                }
            }
            @Override
            public void onFailure(Call<ArrayList<Job>> call, Throwable t) {
                Log.d(TAG, "ON RESPONSE QUOTES FAIL");
                mBus.post(new ApiErrorEvent());
                mBus.post(new JobEvents.QuotesResponse(null));
            }
        });
    }

    @Subscribe
    public void onLoadJobOpportunities(final JobEvents.OpportunitiesRequest event){
        jobOpportunitiesRestApi.getJobOpportunities(event.getToken(), ArmutUtils.clientInfo).enqueue(new Callback<ArrayList<Job>>() {
            @Override
            public void onResponse(Call<ArrayList<Job>> call, Response<ArrayList<Job>> response) {
                Log.d(TAG, "ON RESPONSE opportunities : " + response.isSuccessful() + " - responsecode: " + response.code() + " - response:" + response.message());
                   // Log.d(TAG, "ON RESPONSE opportunity raw: " + response.raw().body().toString());
                if (response.isSuccessful()) {
                    mBus.post(new JobEvents.OpportunitiesResponse(response));
                }
                else {
                    mBus.post(new ApiErrorEvent(response.code(), ArmutUtils.getErrorMessage(response), false));
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Job>> call, Throwable t) {
                Log.d(TAG, "ON RESPONSE OPPORTUNITIES FAIL " + t.getMessage());
                t.printStackTrace();
                mBus.post(new ApiErrorEvent());
                mBus.post(new JobEvents.OpportunitiesResponse(null));
            }
        });
    }

    @Subscribe
    public void onLoadJobDeals(final JobEvents.DealsRequest event){
        jobDealsRestApi.getJobDeals(event.getToken(), ArmutUtils.clientInfo).enqueue(new Callback<ArrayList<Job>>() {
            @Override
            public void onResponse(Call<ArrayList<Job>> call, Response<ArrayList<Job>> response) {
                Log.d(TAG, "ON RESPONSE deals: " + response.isSuccessful() + " - responsecode: " + response.code() + " - response:" + response.message());
                if (response.isSuccessful()) {
                    mBus.post(new JobEvents.DealsResponse(response));
                } else {
                    mBus.post(new ApiErrorEvent(response.code(), ArmutUtils.getErrorMessage(response), false));
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Job>> call, Throwable t) {
                Log.d(TAG, "ON RESPONSE deals: " + t.getMessage());
                mBus.post(new ApiErrorEvent());
                mBus.post(new JobEvents.DealsResponse(null));
            }
        });
    }

    @Subscribe
    public void onLoadJobDetails(final JobEvents.JobDetailRequest event){
        String token = app.isTokenPresent(TAG);
        if(token == null){
            return;
        }
        jobDetailsRestApi.getJobDetails(token, ArmutUtils.clientInfo, event.getJobId()).enqueue(new Callback<JobDetails>() {
            @Override
            public void onResponse(Call<JobDetails> call, Response<JobDetails> response) {
                Log.d(TAG, "ON RESPONSE details: " + response.isSuccessful() + " - responsecode: " + response.code() + " - response:" + response.message());
                if (response.isSuccessful()) {
                    mBus.post(new JobEvents.JobDetailResponse(response));
                } else {
                    if(response.code() != 401){
                        mBus.post(new ApiErrorEvent(response.code(), ArmutUtils.getErrorMessage(response), false));
                    }else{
                        mBus.post(new JobEvents.JobDetailResponse(response));
                    }
                }
            }

            @Override
            public void onFailure(Call<JobDetails> call, Throwable t) {
                Log.d(TAG, "ON RESPONSE details: " + t.getMessage());
                mBus.post(new ApiErrorEvent());
            }
        });
    }

    @Subscribe
    public void onLoadOpportunityRejectReasons(final JobEvents.OpportunityRejectReasonsRequest event){
        String token = app.isTokenPresent(TAG);
        if(token == null){
            return;
        }
        rejectReasonsApi.getJobCancelReasons(token, ArmutUtils.clientInfo).enqueue(new Callback<ArrayList<OpportunityCancelReason>>() {
            @Override
            public void onResponse(Call<ArrayList<OpportunityCancelReason>> call, Response<ArrayList<OpportunityCancelReason>> response) {
                Log.d(TAG, "ON RESPONSE reject reasons: " + response.isSuccessful() + " - responsecode: " + response.code() + " - response:" + response.message());
                if (response.isSuccessful()) {
                    mBus.post(new JobEvents.OpportunityRejectReasonsResponse(response));
                } else {
                    mBus.post(new ApiErrorEvent(response.code(), ArmutUtils.getErrorMessage(response), false));
                }
            }

            @Override
            public void onFailure(Call<ArrayList<OpportunityCancelReason>> call, Throwable t) {
                Log.d(TAG, "ON RESPONSE details: " + t.getMessage());
                mBus.post(new ApiErrorEvent());
            }
        });
    }

    @Subscribe
    public void onPostJobQuote(final JobEvents.PostQuoteRequest event){
        String token = app.isTokenPresent(TAG);
        if(token == null){
            return;
        }
        postQuoteRestApi.isPostQuoteSuccesful(token, ArmutUtils.clientInfo, event.getJobQuote()).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d(TAG, "ON RESPONSE postquote: " + response.isSuccessful() + " - responsecode: " + response.code() + " - response:" + response.message()+ " - url:");
                if (response.isSuccessful()) {
                    mBus.post(new JobEvents.PostQuoteResponse(response, event.getJobQuote()));
                } else {
                    mBus.post(new ApiErrorEvent(response.code(), ArmutUtils.getErrorMessage(response), false));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(TAG, "ON RESPONSE postquote: " + t.getMessage());
                //Gson gson = new Gson();
                //String body = gson.toJson(call.request().body());
                Log.d(TAG, "ON RESPONSE postquote: " + ArmutUtils.bodyToString(call.request().body()));
                mBus.post(new ApiErrorEvent());
            }
        });
    }

    @Subscribe
    public void onPostOpportunityReject(final JobEvents.PostRejectOpportunityRequest event){
        String token = app.isTokenPresent(TAG);
        if(token == null){
            return;
        }
        postOpportunityRejectApi.isOpportunityRejectSuccesful(token, ArmutUtils.clientInfo,event.getJobId(), event.getOpportunityrejectReason()).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d(TAG, "ON RESPONSE reject : " + response.isSuccessful() + " - responsecode: " + response.code() + " - response:" + response.message());
                if (response.isSuccessful()) {
                    mBus.post(new JobEvents.PostRejectOpportunityResponse(response));
                } else {
                    mBus.post(new JobEvents.PostRejectOpportunityResponse(null));
                    mBus.post(new ApiErrorEvent(response.code(), ArmutUtils.getErrorMessage(response), false));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(TAG, "ON RESPONSE details: " + t.getMessage());
                mBus.post(new JobEvents.PostRejectOpportunityResponse(null));
                mBus.post(new ApiErrorEvent());
            }
        });
    }

    @Subscribe
    public void onLoadLeadPrice(final JobEvents.QuoteLeadRequest event){
        String token = app.isTokenPresent(TAG);
        if(token == null){
            return;
        }
        leadPriceRestApi.getQuoteLeadPrice(token, ArmutUtils.clientInfo, event.getJobId(), event.getServiceId()).enqueue(new Callback<QuoteLead>() {
            @Override
            public void onResponse(Call<QuoteLead> call, Response<QuoteLead> response) {
                Log.d(TAG, "ON RESPONSE quoteLead: " + response.isSuccessful() + " - responsecode: " + response.code() + " - response:" + response.message());
                if (response.isSuccessful()) {
                    mBus.post(new JobEvents.QuoteLeadResponse(response));
                } else {
                    mBus.post(new ApiErrorEvent(response.code(), ArmutUtils.getErrorMessage(response), false));
                }
            }

            @Override
            public void onFailure(Call<QuoteLead> call, Throwable t) {
                Log.d(TAG, "ON RESPONSE details: " + t.getMessage());
                mBus.post(new ApiErrorEvent());
            }
        });
    }

    @Subscribe
    public void onLoadCommissionPrice(final JobEvents.CommissionRateRequest event){
        String token = app.isTokenPresent(TAG);
        if(token == null){
            return;
        }
        commissionApi.getCommissionRate(token, ArmutUtils.clientInfo, event.getJobId(), event.getPrice()).enqueue(new Callback<CommissionRate>() {
            @Override
            public void onResponse(Call<CommissionRate> call, Response<CommissionRate> response) {
                Log.d(TAG, "ON RESPONSE commission: " + response.isSuccessful() + " - responsecode: " + response.code() + " - response:" + response.message() + " - url: " + call.request().url());
                if (response.isSuccessful()) {
                    mBus.post(new JobEvents.CommissionRateResponse(response));
                } else {
                    mBus.post(new ApiErrorEvent(response.code(), ArmutUtils.getErrorMessage(response), false));
                }
            }

            @Override
            public void onFailure(Call<CommissionRate> call, Throwable t) {
                Log.d(TAG, "ON RESPONSE details: " + t.getMessage());
                mBus.post(new ApiErrorEvent());
            }
        });
    }
}
