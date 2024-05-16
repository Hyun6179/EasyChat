package com.example.easychat.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;

public class UserModel {
    private String phone;
    private String username;
    private Timestamp createdTimestamp;
    private String userId;
    private String fcmToken;
    private String countryCode;
    private DocumentReference countryCodeRef;

    public UserModel() {
    }

    public UserModel(String phone, String username, Timestamp createdTimestamp, String userId, String countryCode) {
        this.phone = phone;
        this.username = username;
        this.createdTimestamp = createdTimestamp;
        this.userId = userId;
        this.countryCode = countryCode;
    }



    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Timestamp getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(Timestamp createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
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


    public DocumentReference getCountryCodeRef() {
        return countryCodeRef;
    }

    public void setCountryCodeRef(DocumentReference countryCodeRef) {
        this.countryCodeRef = countryCodeRef;
    }
}
