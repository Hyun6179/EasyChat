package com.example.easychat.model;

import com.google.firebase.Timestamp;

public class UserModel {
    private String username;
    private String phone;
    private String userId;
    private String fcmToken;
    private String countryCode;
    private Timestamp createdTimestamp;

    public UserModel() {}

    public UserModel(String username, String phone, String userId, String fcmToken, String countryCode, Timestamp createdTimestamp) {
        this.username = username;
        this.phone = phone;
        this.userId = userId;
        this.fcmToken = fcmToken;
        this.countryCode = countryCode;
        this.createdTimestamp = createdTimestamp;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public Timestamp getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(Timestamp createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }
}
