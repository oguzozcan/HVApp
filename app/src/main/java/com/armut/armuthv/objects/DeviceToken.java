package com.armut.armuthv.objects;

import com.google.gson.annotations.SerializedName;

/**
 * Created by oguzemreozcan on 07/09/16.
 */
public class DeviceToken{

    @SerializedName("device_token")
    private String deviceToken;
    @SerializedName("device_os_id")
    private int deviceosId;
    @SerializedName("application_type")
    private int applicationType;

    public DeviceToken(String deviceToken){
        this.deviceToken = deviceToken;
        this.deviceosId = 0; // 0 means Android
        this.applicationType = 1; // 1 Means HVApp
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }
}
