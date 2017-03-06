package com.armut.armuthv.busevents;

import com.armut.armuthv.objects.CommissionRate;
import com.armut.armuthv.objects.Job;
import com.armut.armuthv.objects.JobCancelReason;
import com.armut.armuthv.objects.JobDetails;
import com.armut.armuthv.objects.JobQuote;
import com.armut.armuthv.objects.OpportunityCancelReason;
import com.armut.armuthv.objects.QuoteLead;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * Created by oguzemreozcan on 17/05/16.
 */
public class JobEvents {

    public static final String PARAM_JOB_ID = "JOB_ID";
    public static final String PARAM_JOB_QUOTE_ID ="JOB_QUOTE_ID";
    public static final String PARAM_PROVIDER_PROFILE_ID = "PROFILE_ID";
    public static final String PARAM_SERVICE_ID = "SERVICE_ID";
    public static final String PARAM_SERVICE_NAME = "SERVICE_NAME";
    public static final String PARAM_CUSTOMER_ID = "CUSTOMER_ID";
    public static final String PARAM_CUSTOMER_NAME = "CUSTOMER_NAME";
    public static final String PARAM_CUSTOMER_PHONE = "CUSTOMER_PHONE";
    public static final String PARAM_JOB_ADDRESS = "ADDRESS";
    public static final String PARAM_JOB_HAS_ADDRESS_DETAILS = "JOB_HAS_ADDRESS_DETAILS";
    public static final String PARAM_JOB_CITY = "CITY";
    public static final String PARAM_JOB_DATE = "DATE";
//    public static final String PARAM_JOB_MODEL = "MODEL";
    public static final String PARAM_LEAD_PRICE = "LEAD_PRICE";
    public static final String PARAM_QUOTE_REMAINING_TIME = "REMAINING_TIME";
    public static final String PARAM_CAN_CALL = "CAN_CALL";
    public static final String PARAM_SECTION_NUMBER = "SECTION_NUMBER";

    public static final String PARAM_JOB_START_DATE = "JOB_START_DATE";
    public static final String PARAM_JOB_START_DATE_TYPE = "JOB_START_DATE_TYPE";
    public static final String PARAM_JOB_QUOTE_PRICE ="QUOTE_PRICE";
    public static final String PARAM_URGENT_JOB= "IS_URGENT";
    public static final String PARAM_PRIVATE_JOB = "IS_PRIVATE";

    public static class OpportunitiesRequest extends EventRequestParent {
        public OpportunitiesRequest(String token, String clientInfo) {
            super(token, clientInfo);
        }
    }

    public static class QuotesRequest extends EventRequestParent {
        public QuotesRequest(String token, String clientInfo) {
            super(token, clientInfo);
        }
    }

    public static class DealsRequest extends EventRequestParent {
        public DealsRequest(String token, String clientInfo) {
            super(token, clientInfo);
        }
    }

    public static class JobDetailRequest{
        private final long jobId;

        public JobDetailRequest(long jobId) {
            this.jobId = jobId;
        }

        public long getJobId() {
            return jobId;
        }
    }

    public static class PostQuoteRequest{
        private final JobQuote jobQuote;

        public PostQuoteRequest(JobQuote jobQuote) {
            this.jobQuote = jobQuote;
        }

        public JobQuote getJobQuote() {
            return jobQuote;
        }
    }

    public static class QuoteLeadRequest{
        private final long jobId;
        private final long serviceId;

        public QuoteLeadRequest(long jobId, long serviceId) {
            this.jobId = jobId;
            this.serviceId = serviceId;
        }

        public long getJobId() {
            return jobId;
        }

        public long getServiceId() {
            return serviceId;
        }
    }

    public static class PostRejectOpportunityRequest{
        private final JobCancelReason reason;
        private final long jobId;

        public PostRejectOpportunityRequest(JobCancelReason reason, long jobId) {
            this.reason = reason;
            this.jobId = jobId;
        }

        public JobCancelReason getOpportunityrejectReason() {
            return reason;
        }

        public long getJobId() {
            return jobId;
        }
    }

    public static class CommissionRateRequest{
        private final double price;
        private final long jobId;
        private final boolean isLeadRequired;

        public CommissionRateRequest(double price, long jobId, boolean isLeadRequired) {
            this.price = price;
            this.jobId = jobId;
            this.isLeadRequired = isLeadRequired;
        }

        public long getJobId() {
            return jobId;
        }

        public double getPrice() {
            return price;
        }

        public boolean isLeadRequired() {
            return isLeadRequired;
        }
    }

    public static class OpportunityRejectReasonsRequest{

    }

    public static class JobDetailResponse{
        private final Response<JobDetails> response;

        public JobDetailResponse(Response<JobDetails> response) {
            this.response = response;
        }

        public Response<JobDetails> getResponse() {
            return response;
        }
    }


    public static class OpportunitiesResponse {
        private final Response<ArrayList<Job>> response;

        public OpportunitiesResponse(Response<ArrayList<Job>> response) {
            this.response = response;
        }

        public Response<ArrayList<Job>> getResponse() {
            return response;
        }
    }

    public static class QuotesResponse {
        private final Response<ArrayList<Job>> response;

        public QuotesResponse(Response<ArrayList<Job>> response) {
            this.response = response;
        }

        public Response<ArrayList<Job>> getResponse() {
            return response;
        }
    }

    public static class DealsResponse {

        private final Response<ArrayList<Job>> response;

        public DealsResponse(Response<ArrayList<Job>> response) {
            this.response = response;
        }

        public Response<ArrayList<Job>> getResponse() {
            return response;
        }

    }

    public static class PostQuoteResponse{
        private final Response<ResponseBody> response;
        private final JobQuote quote;

        public PostQuoteResponse(Response<ResponseBody> response, JobQuote quote) {
            this.response = response;
            this.quote = quote;
        }

        public Response<ResponseBody> getJobQuoteIdJson() {
            return response;
        }

        public JobQuote getQuote() {
            return quote;
        }
    }

    public static class QuoteLeadResponse{
        private final Response<QuoteLead> response;

        public QuoteLeadResponse(Response<QuoteLead> response) {
            this.response = response;
        }

        public Response<QuoteLead> getQuoteLead() {
            return response;
        }
    }

    public static class PostRejectOpportunityResponse{
        private final Response<ResponseBody> response;

        public PostRejectOpportunityResponse(Response<ResponseBody> response) {
            this.response = response;
        }

        public Response<ResponseBody> getRejectOpportunityResponse() {
            return response;
        }
    }

    public static class OpportunityRejectReasonsResponse {
        private final Response<ArrayList<OpportunityCancelReason>> response;

        public OpportunityRejectReasonsResponse(Response<ArrayList<OpportunityCancelReason>> response) {
            this.response = response;
        }

        public Response<ArrayList<OpportunityCancelReason>> getResponse() {
            return response;
        }
    }

    public static class CommissionRateResponse{
        private final Response<CommissionRate> response;

        public CommissionRateResponse(Response<CommissionRate> response) {
            this.response = response;
        }

        public Response<CommissionRate> getResponse() {
            return response;
        }
    }

}
