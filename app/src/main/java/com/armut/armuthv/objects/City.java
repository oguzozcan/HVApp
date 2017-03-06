package com.armut.armuthv.objects;

import com.google.gson.annotations.SerializedName;

/**
 * Created by oguzemreozcan on 21/09/16.
 */

public class City {

    @SerializedName("city_id")
    private int id;
    @SerializedName("city_name")
    private String name;
    @SerializedName("state_id")
    private String stateId;
    @SerializedName("state_name")
    private String stateName;
    @SerializedName("latitude")
    private double latitude;
    @SerializedName("longitude")
    private double longitude;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

//    public String getStateId() {
//        return stateId;
//    }

    public void setStateId(String stateId) {
        this.stateId = stateId;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

//    public double getLongitude() {
//        return longitude;
//    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}


