package com.example.android_gallery_app.model;

import android.os.Environment;

import androidx.appcompat.app.AppCompatActivity;

import com.example.android_gallery_app.presenter.PhotoListPresenter;
import com.example.android_gallery_app.view.MainView;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class PhotoList extends AppCompatActivity implements PhotoListPresenter {
    private List<Photo> list = new ArrayList<Photo>();
    private String currentPhotoPath;
    private int currentPhoto = 0;
    public String fileTxtPathFull;
    private MainView mainView;

    public PhotoList(MainView mainView){

        this.mainView = mainView;
        //writeToFile();

    }

    public Photo addCaption(String caption) {
        Iterator itr=list.iterator();
        Photo photo = null;
        while(itr.hasNext()){
            Photo ph =(Photo)itr.next();
            if (ph.getFile().equals(currentPhotoPath)) {
                ph.setCaption(caption);
                photo = ph;
                break;
                //displayPhoto(ph.getFile());
            }
        }
        writeToFile();
        return photo;
    }

    public void deletePhoto(String mCurrentPhotoPath) throws IOException {
        for (Photo photo: list) {
            if(photo.getFile().equals(mCurrentPhotoPath)) {
                list.remove(photo);
                File file = new File(Environment.getExternalStorageDirectory()
                        .getAbsolutePath(), "/Android/data/com.example.android_gallery_app/files/Pictures");
                File[] fList = file.listFiles();
                if (fList != null) {
                    for (File f : fList) {
                        String split[] = f.getPath().split("\\.");
                        if (!split[split.length - 1].equals(".txt")) {
                            for (int i = 0; i < list.size(); i++) {
                                if (f.getPath().equals(photo.getFile())) {
                                    f.delete();
                                    list.remove(photo);
                                    break;
                                }
                            }
                        }
                    }
                }
                writeToFile();
                break;
            }
        }
    }

    public Photo findPhotos_second(Date startTimestamp, Date endTimestamp, String keywords, String topLeft, String bottomRight) {
        currentPhoto = 0;
        List<Photo> removedPhotos = new ArrayList<Photo>();
        File file = new File(Environment.getExternalStorageDirectory()
                .getAbsolutePath(), "/Android/data/com.example.android_gallery_app/files/Pictures");
        File[] fList = file.listFiles();

        // Remove all the gif files, we do not want these results to show in the search
        for (Photo photo: list) {
            if(photo.getType() != null) {
                removedPhotos.add(photo);
            }
        }
        list.removeAll(removedPhotos);


        if(startTimestamp == null || endTimestamp == null){
            return null;
        }

        if (fList != null) {
            for (File f : fList) {
                String split[] = f.getPath().split("\\.");
                if (!split[split.length-1].equals(".txt")) {
                    if (!(f.lastModified() >= startTimestamp.getTime()
                            && f.lastModified() <= endTimestamp.getTime())) {
                        for (int i = 0; i < list.size(); i++) {
                            if (f.getPath().equals(list.get(i).getFile())) {
                                removedPhotos.add(list.get(i));
                            }
                        }
                    }
                }
            }
        }
        if(topLeft.length() > 0 && bottomRight.length() > 0) {
            String topLeftCoord[] = topLeft.split(",");
            String bottomRightCoord[] = bottomRight.split(",");
            for (Photo photo: list) {
                    if (!(new Double(topLeftCoord[0]) < photo.getLat() && new Double(topLeftCoord[0]) < photo.getLng())
                            && !(new Double(bottomRightCoord[0]) > photo.getLat() && new Double(bottomRightCoord[0]) > photo.getLng())) {
                        removedPhotos.add(photo);
                    }
                }
        }
        if (keywords.length() > 0) {
            for(Photo ph : list) {
                if(ph.getCaption() == null){
                    removedPhotos.add(ph);
                }
                else if (!ph.getCaption().contains(keywords)) {
                    removedPhotos.add(ph);

                }
            }
        }
        System.out.println("HEY LOOK HERE TOO");
        System.out.println(removedPhotos);
        list.removeAll(removedPhotos);
        if(list.isEmpty() == true ) {
            return new Photo("", 0.0 , 0.0, "");
        } else {
            return list.get(0);
        }
    }

    public Photo getPhoto(){
        if(list.size() == 0){
            return null;
        } else if (list.size()< currentPhoto+1){
            currentPhoto = list.size()-1;
        } else if(currentPhoto < 0){
            currentPhoto = 0;
        }
        return list.get(currentPhoto);
    }

    public Photo getPhotoByLocation(String loc){
        for(Photo ph : list) {
            if (ph.getFile().equals(loc)) {
                return ph;
            }
        }
        return null;
    }

    public Photo scrollPhotos(Boolean proc) {
        if(proc){
            if (currentPhoto > 0) {
                currentPhoto--;
            }
        } else {
            if (currentPhoto < (list.size() - 1)) {
                currentPhoto++;
            }
        }
        if(list.size()<1)
            return null;
        Iterator itr=list.iterator();
        int i = 0; Photo photo = list.get(currentPhoto);
        while(itr.hasNext()){
            Photo ph =(Photo)itr.next();
            if (i == currentPhoto) {
                photo = ph;
                currentPhotoPath = photo.getFile();
                break;
            }
            i++;
        }
        return photo;
    }

    @Override
    public void addPhoto(Photo photo, String fileTxtPath) {
        list.add(photo);
        currentPhotoPath = photo.getFile();
        if(photo.getType() == null ){
            fileTxtPathFull = fileTxtPath;
        }
        writeToFile();
    }

    public void addPhoto(Photo photo) {
        list.add(photo);
    }
    public void clearList () {
        list.clear();
    }
    private void writeToFile() {
        FileWriter myWriter = null;
        try {
            if (fileTxtPathFull != null) {
                myWriter = new FileWriter(fileTxtPathFull);
            } else {
                File file = new File(Environment.getExternalStorageDirectory()
                        .getAbsolutePath(), "/Android/data/com.example.android_gallery_app/files/Pictures");
                File[] fList = file.listFiles();
                if (fList != null) {
                    for (File f : fList) {
                        if (f.getPath().contains("myPhotos")) {
                            myWriter = new FileWriter(f.getPath());
                            break;
                        }
                    }
                }
            }

            StringBuilder str = new StringBuilder("");
            for (Photo photo: list) {
                if (photo.getType() != null) {
                    System.out.println("TO STRING: " + photo.toString(true));
                    str.append(photo.toString(true));
                } else {
                    str.append(photo.toString());
                }
            }
            myWriter.append(str);
            myWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void sortList () {
        for (int i=0; i<list.size(); i++) {
            if(list.get(i).getType() != null ) {
                Photo ph = list.get(i);
                int index = list.indexOf(ph);
                list.remove(index);
                list.add(ph);
                i++;
            }
        }
        writeToFile();
    }
}
