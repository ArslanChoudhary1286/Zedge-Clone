package com.myapp.myimages3;

import com.google.firebase.database.Exclude;

public class fvrtModel {
    String fm;
    private String nKey;

    public fvrtModel() {
    }

    public fvrtModel(String fm) {
        this.fm = fm;
    }

    public String getFm() {
        return fm;
    }

    public void setFm(String fm) {
        this.fm = fm;
    }
    @Exclude
    public String getKey() {
        return nKey;
    }
    @Exclude
    public void setKey(String key) {
        nKey = key;
    }
}
