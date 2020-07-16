package com.shliama.augmentedvideotutorial;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class MagazineApiResponse implements Serializable {
    @SerializedName("result")
    ArrayList<MagazineResult> result;
    @SerializedName("status")
    NetworkStatus status;

    public ArrayList<MagazineResult> getResult() {
        return result;
    }

    public void setResult(ArrayList<MagazineResult> result) {
        this.result = result;
    }

    public NetworkStatus getStatus() {
        return status;
    }

    public void setStatus(NetworkStatus status) {
        this.status = status;
    }
}
