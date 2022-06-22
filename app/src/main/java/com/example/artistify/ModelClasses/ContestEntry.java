package com.example.artistify.ModelClasses;

public class ContestEntry {

    private String contestID;
    private String userID;
    private String imageID;
    private String category;
    private long date_submit;
    private String entry_status;
    private int offTopic_count;
    private int vote_count;
    private int evaluation_points;
    private int appeals_points;
    private int order;
    private int rank;
    private int vote_rank;

    public ContestEntry(){

    }

    public ContestEntry(String contestID, String userID, String imageID, String category, long date_submit, String entry_status, int offTopic_count, int vote_count, int evaluation_points, int appeals_points, int order, int rank, int vote_rank) {
        this.contestID = contestID;
        this.userID = userID;
        this.imageID = imageID;
        this.category = category;
        this.date_submit = date_submit;
        this.entry_status = entry_status;
        this.offTopic_count = offTopic_count;
        this.vote_count = vote_count;
        this.evaluation_points = evaluation_points;
        this.appeals_points = appeals_points;
        this.order = order;
        this.rank = rank;
        this.vote_rank = vote_rank;
    }

    public String getContestID() {
        return contestID;
    }

    public void setContestID(String contestID) {
        this.contestID = contestID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getImageID() {
        return imageID;
    }

    public void setImageID(String imageID) {
        this.imageID = imageID;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public long getDate_submit() {
        return date_submit;
    }

    public void setDate_submit(long date_submit) {
        this.date_submit = date_submit;
    }

    public String getEntry_status() {
        return entry_status;
    }

    public void setEntry_status(String entry_status) {
        this.entry_status = entry_status;
    }

    public int getOffTopic_count() {
        return offTopic_count;
    }

    public void setOffTopic_count(int offTopic_count) {
        this.offTopic_count = offTopic_count;
    }

    public int getVote_count() {
        return vote_count;
    }

    public void setVote_count(int vote_count) {
        this.vote_count = vote_count;
    }

    public int getEvaluation_points() {
        return evaluation_points;
    }

    public void setEvaluation_points(int evaluation_points) {
        this.evaluation_points = evaluation_points;
    }

    public int getAppeals_points() {
        return appeals_points;
    }

    public void setAppeals_points(int appeals_points) {
        this.appeals_points = appeals_points;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public int getVote_rank() {
        return vote_rank;
    }

    public void setVote_rank(int vote_rank) {
        this.vote_rank = vote_rank;
    }
}
