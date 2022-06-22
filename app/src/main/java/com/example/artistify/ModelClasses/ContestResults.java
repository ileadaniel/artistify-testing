package com.example.artistify.ModelClasses;

public class ContestResults {

    String contestID;
    String firstPlace; //first place entry id
    String secondPlace; //second place entry id
    String specialPlace; //based on users votes
    String visibility;

    ContestResults(){

    }

    public ContestResults(String contestID, String firstPlace, String secondPlace, String specialPlace, String visibility) {
        this.contestID = contestID;
        this.firstPlace = firstPlace;
        this.secondPlace = secondPlace;
        this.specialPlace = specialPlace;
        this.visibility = visibility;
    }

    public String getContestID() {
        return contestID;
    }

    public void setContestID(String contestID) {
        this.contestID = contestID;
    }

    public String getFirstPlace() {
        return firstPlace;
    }

    public void setFirstPlace(String firstPlace) {
        this.firstPlace = firstPlace;
    }

    public String getSecondPlace() {
        return secondPlace;
    }

    public void setSecondPlace(String secondPlace) {
        this.secondPlace = secondPlace;
    }

    public String getSpecialPlace() {
        return specialPlace;
    }

    public void setSpecialPlace(String specialPlace) {
        this.specialPlace = specialPlace;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }
}
