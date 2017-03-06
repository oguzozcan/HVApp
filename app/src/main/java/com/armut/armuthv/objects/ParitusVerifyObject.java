package com.armut.armuthv.objects;

import com.google.gson.annotations.SerializedName;

/**
 * Created by oguzemreozcan on 11/01/17.
 */

public class ParitusVerifyObject {

    public long jobId;

    @SerializedName("AddressParseResult")
    private Result result;

    public Result getResult() {
        return result;
    }

    public long getJobId() {
        return jobId;
    }

    public void setJobId(long jobId) {
        this.jobId = jobId;
    }

    public class Result{
        @SerializedName("verificationScore")
        private int verificationScore;
        @SerializedName("streetVerificationScore")
        private int streetVerificationScore;
        @SerializedName("verificationType")
        private String verificationType;
        @SerializedName("latitude")
        private double latitude;
        @SerializedName("longitude")
        private double longitude;


        public int getVerificationScore() {
            return verificationScore;
        }

        public String getVerificationType() {
            return verificationType;
        }

        public double getLatitude() {
            return latitude;
        }

        public double getLongitude() {
            return longitude;
        }
    }

    public void setResult(Result result) {
        this.result = result;
    }
}

