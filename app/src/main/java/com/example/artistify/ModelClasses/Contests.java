package com.example.artistify.ModelClasses;

public class Contests {

    private String title,description,category,status,evaluator1_id,evaluator2_id,evaluator3_id,
            appeal_evaluator1_id,appeal_evaluator2_id,appeal_evaluator3_id,
            contest_creator_id,visibility;

    private long timestamp_start;
    private long timestamp_end;
    private long evaluation_duration;
    private long appeal_duration;


    public Contests(){

    }

    public Contests(String title, String description, String category, String status, String evaluator1_id, String evaluator2_id, String evaluator3_id,
                    String appeal_evaluator1_id, String appeal_evaluator2_id, String appeal_evaluator3_id, String contest_creator_id, String visibility,
                    long timestamp_start, long timestamp_end, long evaluation_duration, long appeal_duration) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.status = status;
        this.evaluator1_id = evaluator1_id;
        this.evaluator2_id = evaluator2_id;
        this.evaluator3_id = evaluator3_id;
        this.appeal_evaluator1_id = appeal_evaluator1_id;
        this.appeal_evaluator2_id = appeal_evaluator2_id;
        this.appeal_evaluator3_id = appeal_evaluator3_id;
        this.contest_creator_id = contest_creator_id;
        this.visibility = visibility;
        this.timestamp_start = timestamp_start;
        this.timestamp_end = timestamp_end;
        this.evaluation_duration = evaluation_duration;
        this.appeal_duration = appeal_duration;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEvaluator1_id() {
        return evaluator1_id;
    }

    public void setEvaluator1_id(String evaluator1_id) {
        this.evaluator1_id = evaluator1_id;
    }

    public String getEvaluator2_id() {
        return evaluator2_id;
    }

    public void setEvaluator2_id(String evaluator2_id) {
        this.evaluator2_id = evaluator2_id;
    }

    public String getEvaluator3_id() {
        return evaluator3_id;
    }

    public void setEvaluator3_id(String evaluator3_id) {
        this.evaluator3_id = evaluator3_id;
    }

    public String getAppeal_evaluator1_id() {
        return appeal_evaluator1_id;
    }

    public void setAppeal_evaluator1_id(String appeal_evaluator1_id) {
        this.appeal_evaluator1_id = appeal_evaluator1_id;
    }

    public String getAppeal_evaluator2_id() {
        return appeal_evaluator2_id;
    }

    public void setAppeal_evaluator2_id(String appeal_evaluator2_id) {
        this.appeal_evaluator2_id = appeal_evaluator2_id;
    }

    public String getAppeal_evaluator3_id() {
        return appeal_evaluator3_id;
    }

    public void setAppeal_evaluator3_id(String appeal_evaluator3_id) {
        this.appeal_evaluator3_id = appeal_evaluator3_id;
    }

    public String getContest_creator_id() {
        return contest_creator_id;
    }

    public void setContest_creator_id(String contest_creator_id) {
        this.contest_creator_id = contest_creator_id;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public long getTimestamp_start() {
        return timestamp_start;
    }

    public void setTimestamp_start(long timestamp_start) {
        this.timestamp_start = timestamp_start;
    }

    public long getTimestamp_end() {
        return timestamp_end;
    }

    public void setTimestamp_end(long timestamp_end) {
        this.timestamp_end = timestamp_end;
    }

    public long getEvaluation_duration() {
        return evaluation_duration;
    }

    public void setEvaluation_duration(long evaluation_duration) {
        this.evaluation_duration = evaluation_duration;
    }

    public long getAppeal_duration() {
        return appeal_duration;
    }

    public void setAppeal_duration(long appeal_duration) {
        this.appeal_duration = appeal_duration;
    }
}
