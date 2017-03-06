package com.armut.armuthv.objects;

import com.google.gson.annotations.SerializedName;

/**
 * Created by oguzemreozcan on 21/09/16.
 */

public class State {
    @SerializedName("state_id")
    private int id;
    @SerializedName("state_name")
    private String name;
    @SerializedName("order_id")
    private int orderId;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getOrderId() {
        return orderId;
    }

}
