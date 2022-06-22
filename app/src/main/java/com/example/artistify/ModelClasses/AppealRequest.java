package com.example.artistify.ModelClasses;

public class AppealRequest {

    String contestID;
    String userID;
    String entryID;
    String status;
    String evaluation1_status;
    String evaluator1_answer;
    String evaluation2_status;
    String evaluator2_answer;
    String evaluation3_status;
    String evaluator3_answer;


    AppealRequest(){

    }

    public AppealRequest(String contestID, String userID, String entryID, String status, String evaluation1_status, String evaluator1_answer, String evaluation2_status, String evaluator2_answer, String evaluation3_status, String evaluator3_answer) {
        this.contestID = contestID;
        this.userID = userID;
        this.entryID = entryID;
        this.status = status;
        this.evaluation1_status = evaluation1_status;
        this.evaluator1_answer = evaluator1_answer;
        this.evaluation2_status = evaluation2_status;
        this.evaluator2_answer = evaluator2_answer;
        this.evaluation3_status = evaluation3_status;
        this.evaluator3_answer = evaluator3_answer;
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

    public String getEntryID() {
        return entryID;
    }

    public void setEntryID(String entryID) {
        this.entryID = entryID;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEvaluation1_status() {
        return evaluation1_status;
    }

    public void setEvaluation1_status(String evaluation1_status) {
        this.evaluation1_status = evaluation1_status;
    }

    public String getEvaluator1_answer() {
        return evaluator1_answer;
    }

    public void setEvaluator1_answer(String evaluator1_answer) {
        this.evaluator1_answer = evaluator1_answer;
    }

    public String getEvaluation2_status() {
        return evaluation2_status;
    }

    public void setEvaluation2_status(String evaluation2_status) {
        this.evaluation2_status = evaluation2_status;
    }

    public String getEvaluator2_answer() {
        return evaluator2_answer;
    }

    public void setEvaluator2_answer(String evaluator2_answer) {
        this.evaluator2_answer = evaluator2_answer;
    }

    public String getEvaluation3_status() {
        return evaluation3_status;
    }

    public void setEvaluation3_status(String evaluation3_status) {
        this.evaluation3_status = evaluation3_status;
    }

    public String getEvaluator3_answer() {
        return evaluator3_answer;
    }

    public void setEvaluator3_answer(String evaluator3_answer) {
        this.evaluator3_answer = evaluator3_answer;
    }
}
