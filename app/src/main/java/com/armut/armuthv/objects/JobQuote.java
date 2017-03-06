package com.armut.armuthv.objects;

import com.google.gson.annotations.SerializedName;

/**
 * Created by oguzemreozcan on 29/06/16.
 */
public class JobQuote {

    @SerializedName("job_id")
    private long jobId;
    @SerializedName("provider_profile_id")
    private long providerProfileId;
    @SerializedName("price")
    private double price;
    @SerializedName("job_start_date")
    private String startDate;

    public long getJobId() {
        return jobId;
    }

    public void setJobId(long jobId) {
        this.jobId = jobId;
    }

    public long getProviderProfileId() {
        return providerProfileId;
    }

    public void setProviderProfileId(long providerProfileId) {
        this.providerProfileId = providerProfileId;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }
}
