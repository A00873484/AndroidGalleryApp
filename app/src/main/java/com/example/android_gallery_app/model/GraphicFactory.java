package com.example.android_gallery_app.model;

import com.example.android_gallery_app.presenter.Graphic;

public class GraphicFactory {
    public Graphic getGraphic(String typeOfGraphic, String file, Double lat, Double lng, String timeStamp, String caption){
        if(typeOfGraphic == "PHOTO"){
            if (caption == null){
                return new Photo(file, lat, lng, timeStamp);
            }
            else{
                return new Photo(file, lat, lng, timeStamp, caption);
            }
        }
        else{
            return null;
        }
    }
}
