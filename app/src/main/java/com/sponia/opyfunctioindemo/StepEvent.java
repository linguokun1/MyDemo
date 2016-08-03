package com.sponia.opyfunctioindemo;

/**
 * @author linguokun
 * @packageName com.sponia.opyfunctioindemo
 * @description
 * @date 16/7/29
 */
public class StepEvent {
    public String user_id;
    public float stepCount;
    public String date;
    public float acceleration;
    public String status;

    public StepEvent(String user_id, float stepCount, String date, float acceleration, String status) {
        this.user_id = user_id;
        this.stepCount = stepCount;
        this.date = date;
        this.acceleration = acceleration;
        this.status = status;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public float getStepCount() {
        return stepCount;
    }

    public void setStepCount(float stepCount) {
        this.stepCount = stepCount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public float getAcceleration() {
        return acceleration;
    }

    public void setAcceleration(float acceleration) {
        this.acceleration = acceleration;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
