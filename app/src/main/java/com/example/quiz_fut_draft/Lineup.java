package com.example.quiz_fut_draft;

import android.widget.ImageView;

public class Lineup {
    private String ID;
    private String OVR;
    private ImageView image;

    public Lineup() {
    }

    public Lineup(String ID, String OVR, ImageView image) {
        this.ID = ID;
        this.OVR = OVR;
        this.image = image;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getOVR() {
        return OVR;
    }

    public void setOVR(String OVR) {
        this.OVR = OVR;
    }

    public ImageView getImage() {
        return image;
    }

    public void setImage(ImageView image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "ID='" + ID + '\'' +
                ", OVR='" + OVR + '\'' +
                '}';
    }
}
