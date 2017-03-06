package com.armut.armuthv.objects;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by oguzemreozcan on 30/09/16.
 */

public class Review implements Parcelable {

    @SerializedName("rating")
    private Double rating;
    @SerializedName("review")
    private String review;
    @SerializedName("create_date")
    private String createDate;
    @SerializedName("display_name")
    private String fromUserName;
    @SerializedName("service_name")
    private String serviceName;
    @SerializedName("profile_id")
    private String profileId;

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getFromUserName() {
        return fromUserName;
    }

    public void setFromUserName(String fromUserName) {
        this.fromUserName = fromUserName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getProfileId() {
        return profileId;
    }

    public void setProfileId(String profileId) {
        this.profileId = profileId;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.rating);
        dest.writeString(this.review);
        dest.writeString(this.createDate);
        dest.writeString(this.fromUserName);
        dest.writeString(this.serviceName);
        dest.writeString(this.profileId);
    }

    public Review() {
    }

    protected Review(Parcel in) {
        this.rating = (Double) in.readValue(Double.class.getClassLoader());
        this.review = in.readString();
        this.createDate = in.readString();
        this.fromUserName = in.readString();
        this.serviceName = in.readString();
        this.profileId = in.readString();
    }

    public static final Parcelable.Creator<Review> CREATOR = new Parcelable.Creator<Review>() {
        @Override
        public Review createFromParcel(Parcel source) {
            return new Review(source);
        }

        @Override
        public Review[] newArray(int size) {
            return new Review[size];
        }
    };
}
