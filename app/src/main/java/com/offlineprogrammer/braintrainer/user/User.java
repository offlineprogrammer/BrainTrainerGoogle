package com.offlineprogrammer.braintrainer.user;

import com.google.firebase.database.Exclude;
import com.offlineprogrammer.braintrainer.game.HighScoreGame;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class User {
    private String deviceToken;
    private String firebaseId;
    private String userId;
    private Date dateCreated;
    private HighScoreGame highScoreGame;

    public User(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public User() {

    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("deviceToken", this.deviceToken);
        result.put("highScoreGame", this.highScoreGame.toMap());
        result.put("userId", this.userId);
        result.put("dateCreated", this.dateCreated);
        return result;
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

    public HighScoreGame getHighScoreGame() {
        return highScoreGame;
    }

    public void setHighScoreGame(HighScoreGame highScoreGame) {
        this.highScoreGame = highScoreGame;
    }
}
