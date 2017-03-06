package com.armut.armuthv.objects;

import com.google.gson.annotations.SerializedName;

/**
 * Created by oguzemreozcan on 17/05/16.
 */
public class Job {

    @SerializedName("unread_message_count")
    private int unreadMessageCount;
    @SerializedName("create_date")
    private String createDate;
    @SerializedName("job_id")
    private long jobId;
    @SerializedName("user_id")
    private String userId;
    @SerializedName("service_id")
    private long serviceId;
    @SerializedName("service_name")
    private String serviceName;
    @SerializedName("job_start_datetime")
    private String jobStartDateTime;
    @SerializedName("job_start_type_id")
    private int jobStartTypeId;
    @SerializedName("job_start_type")
    private String jobStartType;
    @SerializedName("job_state")
    private String jobState;
    @SerializedName("job_city")
    private String jobCity;
    @SerializedName("job_district")
    private String jobDistrict;
    @SerializedName("address")
    private String address;
    @SerializedName("private_job_request")
    private boolean isPrivateJobRequest;
    @SerializedName("urgent_job")
    private boolean isUrgentJob;
    @SerializedName("call_preference")
    private String callPreference;
    @SerializedName("job_quote_expire_date")
    private String jobQuoteExpireDate;
    @SerializedName("job_status_id")
    private int jobStatusId;
    @SerializedName("profile_id")
    private long profileId;
//    @SerializedName("lead_price")
//    private Double leadPrice;
    @SerializedName("is_quote_submit_by_me")
    private boolean isQuoteSubmitByMe;
    @SerializedName("detail")
    private String detail;
    @SerializedName("last_message")
    private String lastMessage;
    @SerializedName("question_answers")
    private String questionAnswers;
    @SerializedName("quote_price")
    private Double quotePrice;
    @SerializedName("customer_full_name")
    private String customerFullName;
    @SerializedName("customer_phone_number")
    private String customerPhoneNumber;
    @SerializedName("appointment_date")
    private String appointmentDate;
    @SerializedName("job_latitude")
    private Double latitude;
    @SerializedName("job_longitude")
    private Double longitude;
    @SerializedName("quote_id")
    private int quoteId;
    @SerializedName("is_lead_required")
    private boolean isLeadRequired;
    @SerializedName("has_picture")
    private boolean hasPicture;
    @SerializedName("is_read")
    private boolean isRead;
    @SerializedName("distance")
    private double distance;

    public int getUnreadMessageCount() {
        return unreadMessageCount;
    }

    public void setUnreadMessageCount(int unreadMessageCount) {
        this.unreadMessageCount = unreadMessageCount;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public long getJobId() {
        return jobId;
    }

    public void setJobId(int jobId) {
        this.jobId = jobId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public long getServiceId() {
        return serviceId;
    }

    public void setServiceId(long serviceId) {
        this.serviceId = serviceId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getJobStartDateTime() {
        return jobStartDateTime;
    }

    public void setJobStartDateTime(String jobStartDateTime) {
        this.jobStartDateTime = jobStartDateTime;
    }

    public int getJobStartTypeId() {
        return jobStartTypeId;
    }

    public void setJobStartTypeId(int jobStartTypeId) {
        this.jobStartTypeId = jobStartTypeId;
    }

    public String getJobStartType() {
        return jobStartType;
    }

    public void setJobStartType(String jobStartType) {
        this.jobStartType = jobStartType;
    }

    public String getJobState() {
        if(jobState == null){
            return "";
        }
        return jobState;
    }

    public void setJobState(String jobSatte) {
        this.jobState = jobSatte;
    }

    public String getJobCity() {
        if(jobCity == null){
            return "";
        }
        return jobCity;
    }

    public void setJobCity(String jobCity) {
        this.jobCity = jobCity;
    }

    public String getJobDistrict() {
        if(jobDistrict == null){
            return "";
        }
        return jobDistrict;
    }

    public void setJobDistrict(String jobDistrict) {
        this.jobDistrict = jobDistrict;
    }

    public String getAddress() {
        if(address == null){
            return "";
        }
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isPrivateJobRequest() {
        return isPrivateJobRequest;
    }

    public void setPrivateJobRequest(boolean privateJobRequest) {
        isPrivateJobRequest = privateJobRequest;
    }

    public boolean isUrgentJob() {
        return isUrgentJob;
    }

    public void setUrgentJob(boolean urgentJob) {
        isUrgentJob = urgentJob;
    }

    public String getCallPreference() {
        return callPreference;
    }

    public void setCallPreference(String callPreference) {
        this.callPreference = callPreference;
    }

    public String getJobQuoteExpireDate() {
        return jobQuoteExpireDate;
    }

    public void setJobQuoteExpireDate(String jobQuoteExpireDate) {
        this.jobQuoteExpireDate = jobQuoteExpireDate;
    }

    public int getJobStatusId() {
        return jobStatusId;
    }

    public void setJobStatusId(int jobStatusId) {
        this.jobStatusId = jobStatusId;
    }

    public long getProfileId() {
        return profileId;
    }

    public void setProfileId(int profileId) {
        this.profileId = profileId;
    }

    public boolean isQuoteSubmitByMe() {
        return isQuoteSubmitByMe;
    }

    public void setQuoteSubmitByMe(boolean quoteSubmitByMe) {
        isQuoteSubmitByMe = quoteSubmitByMe;
    }

    public String getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(String appointmentDate) {
        this.appointmentDate = appointmentDate;
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

    public boolean isLeadRequired() {
        return isLeadRequired;
    }

    public void setLeadRequired(boolean leadRequired) {
        isLeadRequired = leadRequired;
    }

    public String getDetail() {
        if(detail == null){
            return "";
        }
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getLastMessage() {
        if(lastMessage == null){
            return "";
        }
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getQuestionAnswers() {
        return questionAnswers;
    }

    public void setQuestionAnswers(String questionAnswers) {
        this.questionAnswers = questionAnswers;
    }

    public Double getQuotePrice() {
        return quotePrice;
    }

    public void setQuotePrice(Double quotePrice) {
        this.quotePrice = quotePrice;
    }

    public String getCustomerFullName() {
        if(customerFullName == null){
            return "";
        }
        return customerFullName;
    }

    public void setCustomerFullName(String customerFullName) {
        this.customerFullName = customerFullName;
    }

    public int getQuoteId() {
        return quoteId;
    }

    public void setQuoteId(int quoteId) {
        this.quoteId = quoteId;
    }

    public void setJobId(long jobId) {
        this.jobId = jobId;
    }

    public void setProfileId(long profileId) {
        this.profileId = profileId;
    }

    public boolean isHasPicture() {
        return hasPicture;
    }

    public void setHasPicture(boolean hasPicture) {
        this.hasPicture = hasPicture;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getCustomerPhoneNumber() {
        return customerPhoneNumber;
    }
}
