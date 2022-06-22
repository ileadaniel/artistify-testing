package com.example.artistify.ModelClasses;

public class EvaluationResult {

    String contestID;
    String entryID;
    String evaluatorID;
    int question_1;
    int question_2;
    int question_3;
    int question_4;
    int question_5;
    int points;


    EvaluationResult(){

    }

    public EvaluationResult(String contestID, String entryID, String evaluatorID, int question_1, int question_2, int question_3, int question_4, int question_5, int points) {
        this.contestID = contestID;
        this.entryID = entryID;
        this.evaluatorID = evaluatorID;
        this.question_1 = question_1;
        this.question_2 = question_2;
        this.question_3 = question_3;
        this.question_4 = question_4;
        this.question_5 = question_5;
        this.points = points;
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

    public String getEvaluatorID() {
        return evaluatorID;
    }

    public void setEvaluatorID(String evaluatorID) {
        this.evaluatorID = evaluatorID;
    }

    public int getQuestion_1() {
        return question_1;
    }

    public void setQuestion_1(int question_1) {
        this.question_1 = question_1;
    }

    public int getQuestion_2() {
        return question_2;
    }

    public void setQuestion_2(int question_2) {
        this.question_2 = question_2;
    }

    public int getQuestion_3() {
        return question_3;
    }

    public void setQuestion_3(int question_3) {
        this.question_3 = question_3;
    }

    public int getQuestion_4() {
        return question_4;
    }

    public void setQuestion_4(int question_4) {
        this.question_4 = question_4;
    }

    public int getQuestion_5() {
        return question_5;
    }

    public void setQuestion_5(int question_5) {
        this.question_5 = question_5;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }
}
