package com.armut.armuthv.objects;

import com.google.gson.annotations.SerializedName;

/**
 * Created by oguzemreozcan on 12/06/15.
 */
public class Profile {

    @SerializedName("profile_id")
    private int profilId;
    @SerializedName("user_id")
    private String userId;
    @SerializedName("service_id")
    private int serviceId;
    @SerializedName("service_name")
    private String serviceName;
    @SerializedName("business_name")
    private String businessName;
    @SerializedName("web_site")
    private String webSite;
    @SerializedName("street_address")
    private String streetAddress;
    @SerializedName("city")
    private int city;
    @SerializedName("state_id")
    private int stateId;
    @SerializedName("state_name")
    private String stateName;
    @SerializedName("city_name")
    private String cityName;
    @SerializedName("zip_code")
    private String zipCode;
    @SerializedName("create_date")
    private String createDate;
    @SerializedName("modified_date")
    private String modifiedDate;
    @SerializedName("credit_card")
    private boolean isCreditCardAvailable;
    @SerializedName("cash")
    private boolean isCashAvailable;
    @SerializedName("money_transfer")
    private boolean isMoneyTransferAvailable;
    @SerializedName("service_description")
    private String serviceDescription;
    @SerializedName("service_search_keywords")
    private String serviceSearchKeywords;
    @SerializedName("profile_control_date")
    private String profileControlDate;
    @SerializedName("district_id")
    private Integer districtId;
    @SerializedName("user_duration")
    private Float userDuration;
    @SerializedName("average_rating")
    private Double averageRating;
    @SerializedName("number_of_completed_jobs")
    private int numberOfCompletedJobs;
    @SerializedName("number_of_comments")
    private int numberOfComments;
    @SerializedName("user_pic_url")
    private String userPicUrl;
    @SerializedName("picture_url")
    private String[] pictureUrl;
    @SerializedName("provider_badges")
    private Badge[] badges;
    @SerializedName("mobile_phone_number")
    private String mobilePhoneNumber;
    @SerializedName("profession_start_date")
    private String professionStartDate;
    @SerializedName("default_profile")
    private boolean isDefaultProfile;
    @SerializedName("business_model")
    private int businessModel;

    public int getProfilId() {
        return profilId;
    }

    public void setProfilId(int profilId) {
        this.profilId = profilId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getServiceId() {
        return serviceId;
    }

    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getWebSite() {
        return webSite;
    }

    public void setWebSite(String webSite) {
        this.webSite = webSite;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public int getCity() {
        return city;
    }

    public void setCity(int city) {
        this.city = city;
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

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(String modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public boolean isCreditCardAvailable() {
        return isCreditCardAvailable;
    }

    public void setIsCreditCardAvailable(boolean isCreditCardAvailable) {
        this.isCreditCardAvailable = isCreditCardAvailable;
    }

    public boolean isCashAvailable() {
        return isCashAvailable;
    }

    public void setIsCashAvailable(boolean isCashAvailable) {
        this.isCashAvailable = isCashAvailable;
    }

    public boolean isMoneyTransferAvailable() {
        return isMoneyTransferAvailable;
    }

    public void setIsMoneyTransferAvailable(boolean isMoneyTransferAvailable) {
        this.isMoneyTransferAvailable = isMoneyTransferAvailable;
    }

    public String getServiceDescription() {
        if(serviceDescription == null){
            return "";
        }
        return serviceDescription;
    }

    public void setServiceDescription(String serviceDescription) {
        this.serviceDescription = serviceDescription;
    }

    public String getServiceSearchKeywords() {
        return serviceSearchKeywords;
    }

    public void setServiceSearchKeywords(String serviceSearchKeywords) {
        this.serviceSearchKeywords = serviceSearchKeywords;
    }

    public String getProfileControlDate() {
        return profileControlDate;
    }

    public void setProfileControlDate(String profileControlDate) {
        this.profileControlDate = profileControlDate;
    }

    public Integer getDistrictId() {
        return districtId;
    }

    public void setDistrictId(Integer districtId) {
        this.districtId = districtId;
    }

    public Float getUserDuration() {
        return userDuration;
    }

    public void setUserDuration(Float userDuration) {
        this.userDuration = userDuration;
    }

    public Double getAverageRating() {
        if (averageRating == null) {
            return 0.0;
        }
        return averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    public int getNumberOfCompletedJobs() {
        return numberOfCompletedJobs;
    }

    public void setNumberOfCompletedJobs(int numberOfCompletedJobs) {
        this.numberOfCompletedJobs = numberOfCompletedJobs;
    }

    public int getNumberOfComments() {
        return numberOfComments;
    }

    public void setNumberOfComments(int numberOfComments) {
        this.numberOfComments = numberOfComments;
    }

    public String[] getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String[] pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public String getUserPicUrl() {
        return userPicUrl;
    }

    public void setUserPicUrl(String userPicUrl) {
        this.userPicUrl = userPicUrl;
    }

    public String getMobilePhoneNumber() {
        return mobilePhoneNumber;
    }

    public void setMobilePhoneNumber(String mobilePhoneNumber) {
        this.mobilePhoneNumber = mobilePhoneNumber;
    }

    public String getProfessionStartDate() {
        return professionStartDate;
    }

    public void setProfessionStartDate(String professionStartDate) {
        this.professionStartDate = professionStartDate;
    }

    public Badge[] getBadges() {
        return badges;
    }

    public void setBadges(Badge[] badges) {
        this.badges = badges;
    }

    public boolean isDefaultProfile() {
        return isDefaultProfile;
    }

    public void setDefaultProfile(boolean defaultProfile) {
        isDefaultProfile = defaultProfile;
    }

    public int getBusinessModel() {
        return businessModel;
    }

    public void setBusinessModel(int businessModel) {
        this.businessModel = businessModel;
    }
}
