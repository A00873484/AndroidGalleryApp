package com.example.android_gallery_app;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ToggleButton;

import com.example.android_gallery_app.model.Photo;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.imageprocessors.subfilters.ColorOverlaySubfilter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;



public class EditActivity extends AppCompatActivity {
    class Meme {
        String text;
        int x, y;
        Paint paint;
        Meme(String text, int x, int y, Paint paint){
            this.text = text;
            this.x = x;
            this.y = y;
            this.paint = paint;
        }
        public String getText(){
            return text;
        }
        public int getX(){
            return x;
        }
        public int getY(){
            return y;
        }
        public void setPosition(int x, int y){
            this.x = x;
            this.y = y;
        }
        public Paint getPaint(){
            return paint;
        }
    }

    Button age;
    Button blackwhite;
    Button reset;
    ImageView imageView;
    static Filter imageFilter;

    boolean prok = false;

    Photo holdPhoto;
    Bitmap holdBit;
    Bitmap currentPhoto;

    Bitmap bitMeme=null; int currentMeme=-1; boolean activeMeme = false; ToggleButton togglemove = null;

    int addlayout = 500;
    int addbutton = 1000;
    int movebutton = 1200;
    int removebutton = 1300;
    int addinput = 1100;
    ArrayList<Integer> ids = new ArrayList<Integer>();
    ArrayList<Meme> textHistory = new ArrayList();

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
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inMutable = true;
        currentPhoto = BitmapFactory.decodeFile(holdPhoto.getFile(), opts);
        holdBit = BitmapFactory.decodeFile(holdPhoto.getFile(), opts);
        this.displayPhoto(holdPhoto);
        addMeme();
        //addMeme();
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

    private Bitmap addAge(Bitmap src, double value) {
        Filter imageFilter = new Filter();
        imageFilter.addSubFilter(new ColorOverlaySubfilter(100, .3f, .2f, .0f));
        Bitmap outputImage = imageFilter.processFilter(currentPhoto);
        return outputImage;
    }

    public void setReset(View view) throws IOException{
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inMutable = true;
        currentPhoto = BitmapFactory.decodeFile(holdPhoto.getFile(), opts);
        imageView.setImageBitmap(holdBit);
        populateText();
    }

    public void setBlackwhite(View view) throws IOException {
        imageView.setImageBitmap(this.createContrast(currentPhoto, 20));
        populateText();
    }

    public void setAge(View view) throws IOException {
        prok = !prok;
        imageView.setImageBitmap(this.addAge(currentPhoto, prok?1:0));
        populateText();
    }

    private void addMeme() {
        LinearLayout layout = (LinearLayout) findViewById(R.id.TextAddLayout);
        LinearLayout sublayout = new LinearLayout(getBaseContext());
        sublayout.setId(++addlayout);
        sublayout.setOrientation(LinearLayout.HORIZONTAL);
        EditText txtTag = new EditText(this);
        txtTag.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        txtTag.setText("Text");
        txtTag.setId(++addinput);
        Button btnTag = new Button(this);
        btnTag.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        btnTag.setText("Add");
        btnTag.setId(++addbutton);
        btnTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    setText(view);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } );
        ToggleButton btnTag2 = new ToggleButton(this);
        btnTag2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        btnTag2.setText("move");
        btnTag2.setId(++movebutton);
        btnTag2.setVisibility(View.GONE);
        btnTag2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    try {
                        setMove(btnTag2);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    disableMove(btnTag2);
                }
            }
        });
        Button btnTag3 = new Button(this);
        btnTag3.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        btnTag3.setText("remove");
        btnTag3.setId(++removebutton);
        btnTag3.setVisibility(View.GONE);
        btnTag3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    setRemove(view);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } );
        //add button to the layout
        sublayout.addView(txtTag);
        sublayout.addView(btnTag);
        sublayout.addView(btnTag2);
        sublayout.addView(btnTag3);
        layout.addView(sublayout);
    }

    public void setText(View view) throws IOException {
        int id = view.getId();
        EditText input = (EditText) findViewById(id+100);
        Button move = (Button) findViewById(id+200);
        Button remove = (Button) findViewById(id+300);
        ids.add(id+200);
        this.addText(input.getText().toString());
        view.setVisibility(View.GONE);
        move.setVisibility(View.VISIBLE);
        remove.setVisibility(View.VISIBLE);
        addMeme();
    }

    public void setMove(View view) throws IOException {
        int id = view.getId();
        int index = ids.indexOf(id);
        if(togglemove != null)
            togglemove.setChecked(false);
        togglemove = (ToggleButton) view;
        Bitmap tempBitmap = Bitmap.createBitmap(currentPhoto.getWidth(), currentPhoto.getHeight(), Bitmap.Config.RGB_565);
        Canvas tempCanvas = new Canvas(tempBitmap);
        tempCanvas.drawBitmap(currentPhoto, 0, 0, null);
        for(int i=0; i<textHistory.size(); i++){
            if(index != i)
                tempCanvas.drawText(textHistory.get(i).getText(), textHistory.get(i).getX(), textHistory.get(i).getY(), textHistory.get(i).getPaint());
        }
        bitMeme = tempBitmap;
        currentMeme = index;
        activeMeme = true;
    }

    public void setRemove(View view) throws IOException {
        int id = view.getId();
        textHistory.remove(id-1301);
        ids.remove(id-1301);
        populateText();
        ((ViewManager)findViewById(id-800).getParent()).removeView(findViewById(id-800));
    }

    public void save(View view) throws IOException {

        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File photoFile = File.createTempFile(imageFileName, ".jpg",storageDir);

        OutputStream os;
        try {
            os = new FileOutputStream(photoFile);
            currentPhoto.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.flush();
            os.close();
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Error writing bitmap", e);
        }
        Intent i = new Intent();
        i.putExtra("NEWPHOTO", photoFile.getAbsolutePath());
        i.putExtra("LAT", holdPhoto.getLat().toString());
        i.putExtra("LNG", holdPhoto.getLng().toString());
        setResult(RESULT_OK, i);
        finish();
    }

    private void disableMove(View view){
        togglemove = null;
        bitMeme = null;
        currentMeme = -1;
        activeMeme = false;
    }

    public void addText(String text) throws IOException {
        Paint pText = new Paint();
        pText.setColor(Color.WHITE);
        pText.setTextSize(100);
        textHistory.add(new Meme(text, 20, 1000, pText));
        populateText();
    }

    private void populateText(){
        Bitmap tempBitmap = Bitmap.createBitmap(currentPhoto.getWidth(), currentPhoto.getHeight(), Bitmap.Config.RGB_565);
        Canvas tempCanvas = new Canvas(tempBitmap);
        tempCanvas.drawBitmap(currentPhoto, 0, 0, null);
        for(int i=0; i<textHistory.size(); i++){
            tempCanvas.drawText(textHistory.get(i).getText(), textHistory.get(i).getX(), textHistory.get(i).getY(), textHistory.get(i).getPaint());
        }
        imageView.setImageDrawable(new BitmapDrawable(getResources(), tempBitmap));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void displayPhoto(Photo photo) {
        if (photo == null) {
            imageView.setImageResource(R.mipmap.ic_launcher);
        } else {
            imageView.setImageBitmap(BitmapFactory.decodeFile(photo.getFile()));
        }
    }

    private void touch_move(float x, float y) {
        Bitmap tempBitmap = Bitmap.createBitmap(bitMeme.getWidth(), bitMeme.getHeight(), Bitmap.Config.RGB_565);
        Canvas tempCanvas = new Canvas(tempBitmap);
        tempCanvas.drawBitmap(bitMeme, 0, 0, null);
        tempCanvas.drawText(textHistory.get(currentMeme).getText(), x, y, textHistory.get(currentMeme).getPaint());
        imageView.setImageDrawable(new BitmapDrawable(getResources(), tempBitmap));
    }

    private void touch_up(float x, float y) {
        textHistory.get(currentMeme).setPosition(((int) x), ((int)y));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(activeMeme) {
            float x = event.getX();
            float y = event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    touch_move(x, y);
                    break;
                case MotionEvent.ACTION_MOVE:
                    touch_move(x, y);
                    break;
                case MotionEvent.ACTION_UP:
                    touch_up(x, y);
                    break;
            }
            return true;
        }
        return false;
    }


}