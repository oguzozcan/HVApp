package com.armut.armuthv.objects;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by oguzemreozcan on 27/06/16.
 */
public class JobDetails implements Parcelable {

    @SerializedName("job_id")
    private int jobId;
    @SerializedName("service_id")
    private int serviceId;
    @SerializedName("service_name")
    private String serviceName;
    @SerializedName("job_status")
    private String jobStatus;
    @SerializedName("job_status_id")
    private int jobStatusId;
    @SerializedName("user_id")
    private String userId;
    @SerializedName("another_service_name")
    private String anotherServiceName;
    @SerializedName("sms_no")
    private String smsNo;
    @SerializedName("special_request_user_profile_id")
    private Integer specialRequestUserProfileId;
    @SerializedName("ip_address")
    private String ipAddress;
    @SerializedName("estimated_price")
    private float estimatedPrice;
    @SerializedName("entry_instructions")
    private String entryInstructions;
    @SerializedName("business_model")
    private int businessModel;
    @SerializedName("discount_code")
    private String discountCode;
    @SerializedName("hour")
    private int hour;
    @SerializedName("job_date")
    private String jobDate;
    @SerializedName("is_repeat")
    private boolean isRepeat;
    @SerializedName("control_jobs_values")
    private ArrayList<ControlJobValue> controlValues;
    @SerializedName("payment_needed")
    private boolean paymentNeeded;
    @SerializedName("rating_needed")
    private boolean ratingNeeded;
    @SerializedName("job_latitude")
    private Double latitude;
    @SerializedName("job_longitude")
    private Double longitude;
    private int color;
    private int groupId;
    //Temporary Stored Quote
    //private Quote quote;
    @SerializedName("call_preference")
    private int callPreference;
    @SerializedName("text_address")
    private String textAddress;

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public int getJobId() {
        return jobId;
    }

    public void setJobId(int jobId) {
        this.jobId = jobId;
    }

    public int getServiceId() {
        return serviceId;
    }

    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAnotherServiceName() {
        return anotherServiceName;
    }

    public void setAnotherServiceName(String anotherServiceName) {
        this.anotherServiceName = anotherServiceName;
    }

    public String getSmsNo() {
        return smsNo;
    }

    public void setSmsNo(String smsNo) {
        this.smsNo = smsNo;
    }

    public int getSpecialRequestUserProfileId() {
        return specialRequestUserProfileId;
    }

    public void setSpecialRequestUserProfileId(Integer specialRequestUserProfileId) {
        this.specialRequestUserProfileId = specialRequestUserProfileId;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public float getEstimatedPrice() {
        return estimatedPrice;
    }

    public void setEstimatedPrice(float estimatedPrice) {
        this.estimatedPrice = estimatedPrice;
    }

    public String getEntryInstructions() {
        return entryInstructions;
    }

    public void setEntryInstructions(String entryInstructions) {
        this.entryInstructions = entryInstructions;
    }

    public boolean isPaymentNeeded() {
        return paymentNeeded;
    }

    public void setPaymentNeeded(boolean paymentNeeded) {
        this.paymentNeeded = paymentNeeded;
    }

    public boolean isRatingNeeded() {
        return ratingNeeded;
    }

    public void setRatingNeeded(boolean ratingNeeded) {
        this.ratingNeeded = ratingNeeded;
    }

    public int getBusinessModel() {
        return businessModel;
    }

    public void setBusinessModel(int businessModel) {
        this.businessModel = businessModel;
    }

    public String getDiscountCode() {
        return discountCode;
    }

    public void setDiscountCode(String discountCode) {
        this.discountCode = discountCode;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public Collection<ControlJobValue> getControlValues() {
        return controlValues;
    }

    public void setControlValues(ArrayList<ControlJobValue> controlValues) {
        this.controlValues = controlValues;
    }

    public String getJobStatus() {
        return jobStatus;
    }

    public void setJobStatus(String jobStatus) {
        this.jobStatus = jobStatus;
    }

    public int getJobStatusId() {
        return jobStatusId;
    }

    public void setJobStatusId(int jobStatusId) {
        this.jobStatusId = jobStatusId;
    }

    public String getJobDate() {
        return jobDate;
    }

    public void setJobDate(String jobDate) {
        this.jobDate = jobDate;
    }

    public boolean isRepeat() {
        return isRepeat;
    }

    public void setIsRepeat(boolean isRepeat) {
        this.isRepeat = isRepeat;
    }


    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public int getCallPreference() {
        return callPreference;
    }

    public void setCallPreference(int callPreference) {
        this.callPreference = callPreference;
    }

    public JobDetails() {
    }

    public String getTextAddress() {
        return textAddress;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.jobId);
        dest.writeInt(this.jobStatusId);
        dest.writeInt(this.serviceId);
        dest.writeString(this.jobStatus);
        dest.writeString(this.serviceName);
        dest.writeString(this.userId);
        dest.writeString(this.anotherServiceName);
        dest.writeString(this.smsNo);
        dest.writeValue(this.specialRequestUserProfileId);
        dest.writeString(this.ipAddress);
        dest.writeFloat(this.estimatedPrice);
        dest.writeString(this.entryInstructions);
        dest.writeInt(this.businessModel);
        dest.writeString(this.discountCode);
        dest.writeInt(this.hour);
        dest.writeString(this.jobDate);
        dest.writeByte(this.isRepeat ? (byte) 1 : (byte) 0);
        dest.writeTypedList(this.controlValues);
        dest.writeByte(this.paymentNeeded ? (byte) 1 : (byte) 0);
        dest.writeByte(this.ratingNeeded ? (byte) 1 : (byte) 0);
        dest.writeValue(this.latitude);
        dest.writeValue(this.longitude);
        dest.writeInt(this.color);
        dest.writeInt(this.groupId);
        //dest.writeParcelable(this.quote, flags);
        dest.writeInt(this.callPreference);
        dest.writeString(this.textAddress);
    }

    protected JobDetails(Parcel in) {
        this.jobId = in.readInt();
        this.jobStatusId = in.readInt();
        this.serviceId = in.readInt();
        this.jobStatus = in.readString();
        this.serviceName = in.readString();
        this.userId = in.readString();
        this.anotherServiceName = in.readString();
        this.smsNo = in.readString();
        this.specialRequestUserProfileId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.ipAddress = in.readString();
        this.estimatedPrice = in.readFloat();
        this.entryInstructions = in.readString();
        this.businessModel = in.readInt();
        this.discountCode = in.readString();
        this.hour = in.readInt();
        this.jobDate = in.readString();
        this.isRepeat = in.readByte() != 0;
        this.controlValues = in.createTypedArrayList(ControlJobValue.CREATOR);
        this.paymentNeeded = in.readByte() != 0;
        this.ratingNeeded = in.readByte() != 0;
        this.latitude = (Double) in.readValue(Double.class.getClassLoader());
        this.longitude = (Double) in.readValue(Double.class.getClassLoader());
        this.color = in.readInt();
        this.groupId = in.readInt();
        //this.quote = in.readParcelable(Quote.class.getClassLoader());
        this.callPreference = in.readInt();
        this.textAddress = in.readString();
    }

    public static final Creator<JobDetails> CREATOR = new Creator<JobDetails>() {
        @Override
        public JobDetails createFromParcel(Parcel source) {
            return new JobDetails(source);
        }

        @Override
        public JobDetails[] newArray(int size) {
            return new JobDetails[size];
        }
    };
}


