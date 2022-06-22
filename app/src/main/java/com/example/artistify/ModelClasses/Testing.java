package com.example.artistify.ModelClasses;

public class Testing {
    private String device;
    private String operation;
    private long elapsedTime;

    public Testing (){

    }

    public Testing(String device, String operation, long elapsedTime) {
        this.device = device;
        this.operation = operation;
        this.elapsedTime = elapsedTime;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public long getElapsedTime() {
        return elapsedTime;
    }

    public void setElapsedTime(long elapsedTime) {
        this.elapsedTime = elapsedTime;
    }
}
