package com.example.android_gallery_app.presenter;

public interface Graphic{
    String getFile();
    String getTimeStamp();
    Double getLng();
    Double getLat();
    void setCaption(String caption);
    String getCaption();
    String toString();
}
