package com.example.android_gallery_app.model;

import com.example.android_gallery_app.presenter.Graphic;

import java.io.Serializable;

public class Photo implements Serializable, Graphic {
    String file, caption, timeStamp;
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
    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
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
}
