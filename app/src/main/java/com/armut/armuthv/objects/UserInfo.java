package com.armut.armuthv.objects;

import com.google.gson.annotations.SerializedName;

/**
 * Created by oguzemreozcan on 13/07/15.
 */
public class UserInfo {

    @SerializedName("user_id")
    private String userId;
    @SerializedName("email")
    private String email;
    @SerializedName("mobile_phone_number")
    private String phoneNumber;
    @SerializedName("first_name")
    private String firstName;
    @SerializedName("last_name")
    private String lastName;
    @SerializedName("password")
    private String password;
    @SerializedName("force_confirm_phone_number")
    private int forceConfirmPhoneNumber;
    @SerializedName("force_create_password")
    private int forceConfirmPassword;
    @SerializedName("pic_url")
    private String picUrl;
    @SerializedName("account_balance")
    private double accountBalance;
    @SerializedName("account_balance_plus_credit")
    private double balancePlusCredit;
    @SerializedName("call_preference")
    private int callPreference;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        if(email == null){
            email = "";
        }
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        if(phoneNumber == null){
            phoneNumber = "";
        }
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getFirstName() {
        if(firstName == null){
            firstName = "";
        }
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        if(lastName == null){
            lastName = "";
        }
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getForceConfirmPhoneNumber() {
        return forceConfirmPhoneNumber;
    }

    public void setForceConfirmPhoneNumber(int forceConfirmPhoneNumber) {
        this.forceConfirmPhoneNumber = forceConfirmPhoneNumber;
    }

    public int getForceConfirmPassword() {
        return forceConfirmPassword;
    }

    public void setForceConfirmPassword(int forceConfirmPassword) {
        this.forceConfirmPassword = forceConfirmPassword;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public double getAccountBalance() {
        return accountBalance;
    }

    public void setAccountBalance(double accountBalance) {
        this.accountBalance = accountBalance;
    }

    public int getCallPreference() {
        return callPreference;
    }

    public void setCallPreference(int callPreference) {
        this.callPreference = callPreference;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public double getBalancePlusCredit() {
        return balancePlusCredit;
    }

    public void setBalancePlusCredit(double balancePlusCredit) {
        this.balancePlusCredit = balancePlusCredit;
    }
}
