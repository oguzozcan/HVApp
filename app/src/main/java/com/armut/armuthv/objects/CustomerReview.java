package com.armut.armuthv.objects;

import com.google.gson.annotations.SerializedName;

/**
 * Created by oguzemreozcan on 14/02/17.
 */

public class CustomerReview {
    @SerializedName("customer_review")
    private String review;

    public CustomerReview(String review) {
        this.review = review;
    }

    public String getReview() {
        return review;
    }
}
