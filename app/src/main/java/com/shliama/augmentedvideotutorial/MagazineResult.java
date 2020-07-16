package com.shliama.augmentedvideotutorial;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class MagazineResult implements Serializable {
    @SerializedName("version")
    int version;
    @SerializedName("title")
    String title;
    @SerializedName("publishDate")
    String publishDate;

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }
}
