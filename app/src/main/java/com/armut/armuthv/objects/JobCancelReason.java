package com.armut.armuthv.objects;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by oguzemreozcan on 30/06/16.
 */
public class JobCancelReason implements Parcelable {

    @SerializedName("id")
    private int reasonId;
    @SerializedName("answer")
    private String reason;
    @SerializedName("user_id")
    private String userId;
    @SerializedName("job_id")
    private int jobId;

    public int getReasonId() {
        return reasonId;
    }

    public void setReasonId(int reasonId) {
        this.reasonId = reasonId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public JobCancelReason() {
    }

    public JobCancelReason(int reasonId, String reason, String userId, int jobId) {
        this.reasonId = reasonId;
        this.reason = reason;
        this.userId = userId;
        this.jobId = jobId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.reasonId);
        dest.writeString(this.reason);
        dest.writeString(this.userId);
        dest.writeInt(this.jobId);
    }

    protected JobCancelReason(Parcel in) {
        this.reasonId = in.readInt();
        this.reason = in.readString();
        this.userId = in.readString();
        this.jobId = in.readInt();
    }

    public static final Parcelable.Creator<JobCancelReason> CREATOR = new Parcelable.Creator<JobCancelReason>() {
        @Override
        public JobCancelReason createFromParcel(Parcel source) {
            return new JobCancelReason(source);
        }

        @Override
        public JobCancelReason[] newArray(int size) {
            return new JobCancelReason[size];
        }
    };
}

