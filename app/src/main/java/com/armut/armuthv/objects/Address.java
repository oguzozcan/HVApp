package com.armut.armuthv.objects;

import com.google.gson.annotations.SerializedName;

/**
 * Created by oguzemreozcan on 12/08/16.
 */
public class Address {
    @SerializedName("address")
    private String address = "";
    @SerializedName("city_id")
    private int cityId;
    @SerializedName("city_name")
    private String cityName = "";
    @SerializedName("state_id")
    private int stateId;
    @SerializedName("state_name")
    private String stateName = "";
    @SerializedName("district_id")
    private int districtId;
    @SerializedName("district_name")
    private String districtName = "";

    public String getAddress() {
        if(address == null){
            address = "";
        }
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public String getCityName() {
        if(cityName == null){
            cityName = "";
        }
        return cityName;
    }


    public int getStateId() {
        return stateId;
    }

    public void setStateId(int stateId) {
        this.stateId = stateId;
    }

    public String getStateName() {
        return stateName;
    }


    public int getDistrictId() {
        return districtId;
    }

    public void setDistrictId(int districtId) {
        this.districtId = districtId;
    }

    public String getDistrictName() {
        if(districtName == null){
            districtName = "";
        }
        return districtName;
    }
}

