package com.armut.armuthv.objects;

import com.google.gson.annotations.SerializedName;

/**
 * Created by oguzemreozcan on 30/06/16.
 */
public class QuoteLead {

    @SerializedName("is_lead_required")
    private final boolean isLeadRequired;
    @SerializedName("lead_price")
    private final Double leadPrice;

    public QuoteLead(boolean isLeadRequired, Double leadPrice) {
        this.isLeadRequired = isLeadRequired;
        this.leadPrice = leadPrice;
    }

    public boolean isLeadRequired() {
        return isLeadRequired;
    }

    public Double getLeadPrice() {
        return leadPrice;
    }
}
