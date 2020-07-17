package com.shliama.augmentedvideotutorial.Network;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class NetworkStatus implements Serializable {
    @SerializedName("message")
    String message;
    @SerializedName("isSuccess")
    boolean isSuccess;
    @SerializedName("statusCode")
    int statusCode;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
}
