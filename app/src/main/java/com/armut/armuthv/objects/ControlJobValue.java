package com.armut.armuthv.objects;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by oguzemreozcan on 27/06/16.
 */
public class ControlJobValue implements Parcelable {

    @SerializedName("control_id")
    private int controlId;
    @SerializedName("value")
    private String value;
    @SerializedName("label")
    private String label;

    public int getControlId() {
        return controlId;
    }

    public void setControlId(int controlId) {
        this.controlId = controlId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.controlId);
        dest.writeString(this.value);
        dest.writeString(this.label);
    }

    public ControlJobValue() {
    }

    private ControlJobValue(Parcel in) {
        this.controlId = in.readInt();
        this.value = in.readString();
        this.label = in.readString();
    }

    public static final Parcelable.Creator<ControlJobValue> CREATOR = new Parcelable.Creator<ControlJobValue>() {
        public ControlJobValue createFromParcel(Parcel source) {
            return new ControlJobValue(source);
        }

        public ControlJobValue[] newArray(int size) {
            return new ControlJobValue[size];
        }
    };
}
