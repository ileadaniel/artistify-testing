package com.example.artistify.ModelClasses;

public class ContestVotes {

    String userID;
    String contestID;
    String entryID;

    ContestVotes(){

    }

    public ContestVotes(String userID, String contestID, String entryID) {
        this.userID = userID;
        this.contestID = contestID;
        this.entryID = entryID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getContestID() {
        return contestID;
    }

    public void setContestID(String contestID) {
        this.contestID = contestID;
    }

    public String getEntryID() {
        return entryID;
    }

    public void setEntryID(String entryID) {
        this.entryID = entryID;
    }
}
