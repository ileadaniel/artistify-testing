package com.example.artistify.ModelClasses;

public class EvaluationRequest {

    private String contest_id,evaluator_id,invite_status;

    private EvaluationRequest(){ }

    public EvaluationRequest(String contest_id, String evaluator_id, String invite_status) {
        this.contest_id = contest_id;
        this.evaluator_id = evaluator_id;
        this.invite_status = invite_status;
    }

    public String getContest_id() {
        return contest_id;
    }

    public void setContest_id(String contest_id) {
        this.contest_id = contest_id;
    }


    public String getEvaluator_id() {
        return evaluator_id;
    }

    public void setEvaluator_id(String evaluator_id) {
        this.evaluator_id = evaluator_id;
    }

    public String getInvite_status() {
        return invite_status;
    }

    public void setInvite_status(String invite_status) {
        this.invite_status = invite_status;
    }
}
