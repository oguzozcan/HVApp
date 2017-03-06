package com.armut.armuthv.objects;

import com.google.gson.annotations.SerializedName;

/**
 * Created by oguzemreozcan on 06/09/16.
 */
public class UserCalendarItem {

    @SerializedName("date")
    private String date;
    @SerializedName("day_start_hour")
    private Integer dayStartHour;
    @SerializedName("day_end_hour")
    private Integer dayEndHour;
    @SerializedName("working")
    private boolean working;

    private String nameOfDay;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getDayStartHour() {
        if(dayStartHour != null)
            return dayStartHour;
        else
            return 8;
    }

    public void setDayStartHour(int dayStartHour) {
        this.dayStartHour = dayStartHour;
    }

    public int getDayEndHour() {
        if(dayEndHour != null)
            return dayEndHour;
        else{
            return 18;
        }
    }

    public void setDayEndHour(int dayEndHour) {
        this.dayEndHour = dayEndHour;
    }

    public boolean isWorking() {
        return working;
    }

    public void setWorking(boolean working) {
        this.working = working;
    }

    public String getNameOfDay() {
        return nameOfDay;
    }

    public void setNameOfDay(String nameOfDay) {
        this.nameOfDay = nameOfDay;
    }
}
