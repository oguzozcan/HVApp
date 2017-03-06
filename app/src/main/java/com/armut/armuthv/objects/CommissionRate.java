package com.armut.armuthv.objects;

import com.google.gson.annotations.SerializedName;

/**
 * Created by oguzemreozcan on 11/07/16.
 */
public class CommissionRate {
    @SerializedName("commission_rate")
    double commissionRate;

    public double getCommissionRate() {
        return commissionRate;
    }
}
