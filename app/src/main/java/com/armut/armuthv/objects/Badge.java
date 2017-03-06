package com.armut.armuthv.objects;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by oguzemreozcan on 31/08/16.
 */
public class Badge implements Parcelable {

    @SerializedName("badge_id")
    private int badgeId;
    @SerializedName("badge_type_id")
    private int badgeTypeId;
    @SerializedName("create_date")
    private String createDate;
    @SerializedName("description")
    private String description;
    @SerializedName("pic_url")
    private String picUrl;
    @SerializedName("title")
    private String title;
    @SerializedName("user_id")
    private String userId;

    public int getBadgeId() {
        return badgeId;
    }

    public void setBadgeId(int badgeId) {
        this.badgeId = badgeId;
    }

    public int getBadgeTypeId() {
        return badgeTypeId;
    }

    public void setBadgeTypeId(int badgeTypeId) {
        this.badgeTypeId = badgeTypeId;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.badgeId);
        dest.writeInt(this.badgeTypeId);
        dest.writeString(this.createDate);
        dest.writeString(this.description);
        dest.writeString(this.picUrl);
        dest.writeString(this.title);
        dest.writeString(this.userId);
    }

    public Badge() {
    }

    private Badge(Parcel in) {
        this.badgeId = in.readInt();
        this.badgeTypeId = in.readInt();
        this.createDate = in.readString();
        this.description = in.readString();
        this.picUrl = in.readString();
        this.title = in.readString();
        this.userId = in.readString();
    }

    public static final Parcelable.Creator<Badge> CREATOR = new Parcelable.Creator<Badge>() {
        public Badge createFromParcel(Parcel source) {
            return new Badge(source);
        }

        public Badge[] newArray(int size) {
            return new Badge[size];
        }
    };
}