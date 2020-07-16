package com.shliama.augmentedvideotutorial;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class AssetResult implements Serializable {
    @SerializedName("id")
    String id;
    @SerializedName("inputImageUrl")
    String inputImageUrl;
    @SerializedName("outPutFileUrl")
    String outPutFileUrl;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getInputImageUrl() {
        return inputImageUrl;
    }

    public void setInputImageUrl(String inputImageUrl) {
        this.inputImageUrl = inputImageUrl;
    }

    public String getOutPutFileUrl() {
        return outPutFileUrl;
    }

    public void setOutPutFileUrl(String outPutFileUrl) {
        this.outPutFileUrl = outPutFileUrl;
    }
}

