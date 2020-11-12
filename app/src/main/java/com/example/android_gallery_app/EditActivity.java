package com.example.android_gallery_app;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.android_gallery_app.model.Photo;

import java.io.IOException;

public class EditActivity extends AppCompatActivity {

    Button age;
    Button blackwhite;
    Button reset;
    ImageView imageView;

    boolean prok = false;

    Photo holdPhoto;
    Photo currentPhoto;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        age = (Button) findViewById(R.id.age);
        blackwhite = (Button) findViewById(R.id.blackwhite);
        reset = (Button) findViewById(R.id.reset);
        imageView = findViewById(R.id.imageView);
        holdPhoto = (Photo) getIntent().getSerializableExtra("PHOTO");
        currentPhoto = holdPhoto;
        this.displayPhoto(currentPhoto);

    }

    private static Bitmap createContrast(Bitmap src, double value) {
        // image size
        int width = src.getWidth();
        int height = src.getHeight();
        // create output bitmap
        Bitmap bmOut = Bitmap.createBitmap(width, height, src.getConfig());
        // color information
        int A, R, G, B;
        int pixel;
        // get contrast value
        double contrast = Math.pow((100 + value) / 100, 2);

        // scan through all pixels
        for(int x = 0; x < width; ++x) {
            for(int y = 0; y < height; ++y) {
                // get pixel color
                pixel = src.getPixel(x, y);
                A = Color.alpha(pixel);
                // apply filter contrast for every channel R, G, B
                R = Color.red(pixel);
                R = (int)(((((R / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
                if(R < 0) { R = 0; }
                else if(R > 255) { R = 255; }

                G = Color.red(pixel);
                G = (int)(((((G / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
                if(G < 0) { G = 0; }
                else if(G > 255) { G = 255; }

                B = Color.red(pixel);
                B = (int)(((((B / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
                if(B < 0) { B = 0; }
                else if(B > 255) { B = 255; }

                // set new pixel color to output bitmap
                bmOut.setPixel(x, y, Color.argb(A, R, G, B));
            }
        }

        return bmOut;
    }

    private static Bitmap addAge(Bitmap src, double value) {
        int width = src.getWidth();
        int height = src.getHeight();
        Bitmap bmOut = Bitmap.createBitmap(width, height, src.getConfig());
        /*int A, R, G, B;
        int pixel;
        double contrast = Math.pow((100 + value) / 100, 2);
        for(int x = 0; x < width; ++x) {
            for(int y = 0; y < height; ++y) {
                pixel = src.getPixel(x, y);
                A = Color.alpha(pixel);
                R = Color.red(pixel);
                //R = (int)(R - 10);
                if(R < 0) { R = 0; }
                else if(R > 255) { R = 255; }

                G = Color.red(pixel);
                //G = (int)(G - 10);
                if(G < 0) { G = 0; }
                else if(G > 255) { G = 255; }

                B = Color.red(pixel);
                //B = (int)(B - 10);
                if(B < 0) { B = 0; }
                else if(B > 255) { B = 255; }

                bmOut.setPixel(x, y, Color.argb(A, R, G, B));
            }
        }*/
        return bmOut;
    }

    public void setReset(View view) throws IOException{
        Bitmap bitmap = BitmapFactory.decodeFile(currentPhoto.getFile());
        imageView.setImageBitmap(bitmap);
    }

    public void setBlackwhite(View view) throws IOException {
        Bitmap bitmap = BitmapFactory.decodeFile(currentPhoto.getFile());
        imageView.setImageBitmap(this.createContrast(bitmap, 20));
    }

    public void setAge(View view) throws IOException {
        prok = !prok;
        Bitmap bitmap = BitmapFactory.decodeFile(currentPhoto.getFile());
        imageView.setImageBitmap(this.addAge(bitmap, prok?1:0));
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void displayPhoto(Photo photo) {
        if (photo == null) {
            imageView.setImageResource(R.mipmap.ic_launcher);
        } else {
            imageView.setImageBitmap(BitmapFactory.decodeFile(photo.getFile()));
        }
    }

}