package com.offlineprogrammer.braintrainer.user;

import java.util.Date;

public class User {
    private String deviceToken;
    private String firebaseId;
    private String userId;
    private Date dateCreated;

    public User(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public User() {

    }


    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public String getFirebaseId() {
        return firebaseId;
    }

    public void setFirebaseId(String firebaseId) {
        this.firebaseId = firebaseId;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
