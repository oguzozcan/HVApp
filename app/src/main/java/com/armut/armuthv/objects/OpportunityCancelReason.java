package com.armut.armuthv.objects;

import com.google.gson.annotations.SerializedName;

/**
 * Created by oguzemreozcan on 13/10/16.
 */

public class OpportunityCancelReason {

    @SerializedName("reason_id")
    private int reasonId;
    @SerializedName("reason")
    private String reason;

    public OpportunityCancelReason(int reasonId, String reason) {
        this.reasonId = reasonId;
        this.reason = reason;
    }

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
}
