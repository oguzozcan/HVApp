package com.armut.armuthv.services;

import com.armut.armuthv.objects.CommissionRate;
import com.armut.armuthv.objects.Job;
import com.armut.armuthv.objects.JobCancelReason;
import com.armut.armuthv.objects.JobDetails;
import com.armut.armuthv.objects.JobQuote;
import com.armut.armuthv.objects.OpportunityCancelReason;
import com.armut.armuthv.objects.QuoteLead;
import com.armut.armuthv.utils.Constants;

import java.util.ArrayList;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by oguzemreozcan on 17/05/16.
 */
public class JobRestApi {

    public interface JobOpportunitiesRestApi{
        @GET(Constants.JOB_OPPORTUNITIES_POSTFIX)
        Call<ArrayList<Job>> getJobOpportunities(@Header("Authorization") String token, @Header("client_info") String clientInfo);
    }

    public interface JobQuotesRestApi{
        @GET(Constants.JOB_QUOTES_POSTFIX)
        Call<ArrayList<Job>> getJobQuotes(@Header("Authorization") String token, @Header("client_info") String clientInfo);
    }

    public interface JobDealsRestApi{
        @GET(Constants.JOB_DEALS_POSTFIX)
        Call<ArrayList<Job>> getJobDeals(@Header("Authorization") String token, @Header("client_info") String clientInfo);
    }

    public interface JobDetailsRestApi{
        @GET(Constants.JOB_DETAILS_POSTFIX +"{job_id}")
        Call<JobDetails> getJobDetails(@Header("Authorization") String token, @Header("client_info") String clientInfo, @Path("job_id") long jobId);
    }

    public interface PostQuoteRestApi{
        @POST("job_quotes")
        Call<ResponseBody> isPostQuoteSuccesful(@Header("Authorization") String token, @Header("client_info") String clientInfo, @Body JobQuote jsonBody );
    }

    public interface LeadPriceRestApi{
        @GET(Constants.JOB_QUOTES_LEAD_POSTFIX +"{job_id}")
        Call<QuoteLead> getQuoteLeadPrice(@Header("Authorization") String token, @Header("client_info") String clientInfo, @Path("job_id") long jobId, @Query("service_id") long serviceId);
    }

    public interface JobOpportunityRejectApi{
        @POST(Constants.JOB_OPPORTUNITIES_REJECT_POSTFIX)
        Call<ResponseBody> isOpportunityRejectSuccesful(@Header("Authorization") String token, @Header("client_info") String clientInfo, @Path("job_id") long jobId, @Body JobCancelReason jsonBody );
    }

    public interface RejectReasonsApi{
        @GET(Constants.JOB_OPPORTUNITIES_REJECT_REASONS_POSTFIX)
        Call<ArrayList<OpportunityCancelReason>> getJobCancelReasons(@Header("Authorization") String token, @Header("client_info") String clientInfo);
    }

    public interface JobQuoteCommissionApi{
        @GET(Constants.JOB_QUOTES_COMMISSION_RATE +"{job_id}")
        Call<CommissionRate> getCommissionRate(@Header("Authorization") String token, @Header("client_info") String clientInfo, @Path("job_id") long jobId, @Query("price") double price );
    }

    public interface UploadJobPhoto{
        //image/jpeg
//        RequestBody body = null;
//        if(encodedImage != null) {
//            body = RequestBody.create(IMAGE, encodedImage);
//        }
        @POST(Constants.JOB_PHOTO_UPLOAD_POSTFIX)
        Call<Void> uploadProfilePhoto(@Header("Authorization") String token, @Header("client_info") String clientInfo, @Header("Content-Type") String contentType,
                                      @Query("batch_id") String batchId, @Query("image_id") String imageId, @Body RequestBody encodedImage);
    }
}
