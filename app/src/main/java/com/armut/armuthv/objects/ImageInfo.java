package com.armut.armuthv.objects;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by oguzemreozcan on 27/06/16.
 */
public class ImageInfo implements Parcelable {

    private String imageId;
    private String batchId;
    private byte[] imageEncoded;
    //Incase if reloading needed
    private String path;

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public byte[] getImageEncoded() {
        return imageEncoded;
    }

    public void setImageEncoded(byte[] imageEncoded) {
        this.imageEncoded = imageEncoded;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.imageId);
        dest.writeString(this.batchId);
        dest.writeString(this.path);
        dest.writeByteArray(this.imageEncoded);
    }

    public ImageInfo() {
    }

    private ImageInfo(Parcel in) {
        this.imageId = in.readString();
        this.batchId = in.readString();
        this.path = in.readString();
        this.imageEncoded = in.createByteArray();
    }

    public static final Parcelable.Creator<ImageInfo> CREATOR = new Parcelable.Creator<ImageInfo>() {
        public ImageInfo createFromParcel(Parcel source) {
            return new ImageInfo(source);
        }

        public ImageInfo[] newArray(int size) {
            return new ImageInfo[size];
        }
    };
}

