package com.example.android_gallery_app.model;

import com.example.android_gallery_app.presenter.Graphic;

import java.io.Serializable;

public class Photo implements Serializable, Graphic {
    String file, caption, timeStamp, type;
    Double lat, lng;

    Photo(String file, Double lat, Double lng, String timeStamp) {
        this.file = file;
        this.lat = lat;
        this.lng = lng;
        this.timeStamp = timeStamp;
    }
    public Photo(String file, Double lat, Double lng, String timeStamp, String caption) {
        this.file = file;
        this.lat = lat;
        this.lng = lng;
        this.timeStamp = timeStamp;
        this.caption = caption;
    }
    public Photo(String file) {
        this.file = file;
        this.type = "gif";
    }
    public String getFile() {
        return file;
    }

<<<<<<< HEAD
    public String getType() {
        return type;
=======
    public void setFile(String file) {
        this.file = file;
>>>>>>> a85eb0d87d4d28e82c2e3ee18aa205317ab5eda3
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public Double getLng() {
        return lng;
    }

    public Double getLat() {
        return lat;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getCaption() {
        return caption;
    }
    @Override
    public String toString() {
        return  this.getFile()
                + "," + this.getLng()
                + "," + this.getLat()
                + "," + this.getTimeStamp()
                + "," + this.getCaption()
                + "\n";
    }
    public String toString(boolean isGif) {
        return  this.getFile()
                + "," + this.getType()
                + "\n";
    }
}
