package com.myapp.myimages3;

public class fvrtClass {
    String imageUrl;
    int position;

    public fvrtClass() {
    }

    public fvrtClass(String imageUrl, int position) {
        this.imageUrl = imageUrl;
        this.position = position;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
