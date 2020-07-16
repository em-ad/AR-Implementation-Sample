package com.shliama.augmentedvideotutorial;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class AssetApiResponse implements Serializable {
    @SerializedName("result")
    ArrayList<AssetResult> result;
    @SerializedName("status")
    NetworkStatus status;

    public ArrayList<AssetResult> getResult() {
        return result;
    }

    public void setResult(ArrayList<AssetResult> result) {
        this.result = result;
    }

    public NetworkStatus getStatus() {
        return status;
    }

    public void setStatus(NetworkStatus status) {
        this.status = status;
    }
}
