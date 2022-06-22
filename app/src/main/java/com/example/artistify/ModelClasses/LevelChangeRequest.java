package com.example.artistify.ModelClasses;

public class LevelChangeRequest {

    String userID;
    int level_requested;

    LevelChangeRequest(){

    }

    public LevelChangeRequest(String userID, int level_requested) {
        this.userID = userID;
        this.level_requested = level_requested;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public int getLevel_requested() {
        return level_requested;
    }

    public void setLevel_requested(int level_requested) {
        this.level_requested = level_requested;
    }
}
