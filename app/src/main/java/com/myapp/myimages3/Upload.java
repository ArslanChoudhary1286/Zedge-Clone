package com.myapp.myimages3;


import com.google.firebase.database.Exclude;

public class Upload {
    private String mImageUrl;
    private String fvrtImageUrl;
    private String mKey;

    public Upload() {
    }

    public Upload(String mImageUrl, String fvrtImageUrl) {
        this.mImageUrl = mImageUrl;
        this.fvrtImageUrl = fvrtImageUrl;
    }

    public Upload(String mImageUrl) {
        this.mImageUrl = mImageUrl;
    }

    public String getmImageUrl() {
        return mImageUrl;
    }

    public void setmImageUrl(String mImageUrl) {
        this.mImageUrl = mImageUrl;
    }

    public String getFvrtImageUrl() {
        return fvrtImageUrl;
    }

    public void setFvrtImageUrl(String fvrtImageUrl) {
        this.fvrtImageUrl = fvrtImageUrl;
    }

    @Exclude
    public String getKey() {
        return mKey;
    }
    @Exclude
    public void setKey(String key) {
        mKey = key;
    }

}