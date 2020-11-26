package com.example.android_gallery_app;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.cloudinary.Transformation;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.evernote.android.job.JobApi;
import com.evernote.android.job.JobConfig;
import com.example.android_gallery_app.model.GraphicFactory;
import com.example.android_gallery_app.model.Photo;
import com.example.android_gallery_app.model.PhotoList;
import com.example.android_gallery_app.presenter.PhotoListPresenter;
import com.example.android_gallery_app.view.MainView;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.text.DateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity implements Serializable, MainView {
    static
    {
        System.loadLibrary("NativeImageProcessor");
    }

    private PhotoListPresenter photoListPresenter;

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int SELECT_VIDEO  = 2;
    static final int EDIT_ACTIVITY_REQUEST_CODE = 80;
    private String gifUrl;
    String mCurrentPhotoPath, photosFilePath;

    Button btnNext;
    Button btnPrev;
    Button reset;
    ImageView imageView;
    EditText caption;
    TextView time;
    ProgressBar progressBar;

    static final int SEARCH_ACTIVITY_REQUEST_CODE = 88;
    private FusedLocationProviderClient fusedLocationClient;
    GraphicFactory graphicFactory;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        MediaManager.init(this);
        btnNext = (Button) findViewById(R.id.btnNext);
        btnPrev = (Button) findViewById(R.id.btnPrev);
        imageView = findViewById(R.id.ivGallery);
        caption = (EditText) findViewById(R.id.etCaption);
        time = (TextView) findViewById(R.id.tvTimestamp);
        reset = (Button) findViewById(R.id.reset);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        photoListPresenter = new PhotoList(MainActivity.this);
        graphicFactory = new GraphicFactory();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        try {
            showOriginalView();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void showOriginalView() throws IOException {
        boolean fileExists = false;
        File file = new File(Environment.getExternalStorageDirectory()
                .getAbsolutePath(), "/Android/data/com.example.android_gallery_app/files/Pictures");
        File[] fList = file.listFiles();
        if (fList != null) {
            for (File f : fList) {
                if (f.getPath().contains("myPhotos")) {
                    photosFilePath = f.getPath();
                    fileExists = true;
                    File myObj = new File(photosFilePath);
                    photoListPresenter.clearList();
                    Scanner myReader = new Scanner(myObj);
                    while (myReader.hasNextLine()) {
                        String data = myReader.nextLine();
                        if( data.contains("gif")) {
                            photoListPresenter.addPhoto(new Photo(data.substring(0, data.length() - 4)));
                        }
                        else {
                            String arr[] = data.split(",");
                            String caption;
                            if (arr.length == 4) {
                                caption = "";
                            } else {
                                caption = arr[4];
                            }
                            photoListPresenter.addPhoto(new Photo(arr[0], new Double(arr[2]), new Double(arr[1]), arr[3], caption));
                        }
                    }
                    displayPhoto(photoListPresenter.getPhoto());
                    break;
                }
            }
        }
        if (fileExists == false) {
            File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            File photosFile = File.createTempFile("myPhotos", ".txt",storageDir);
            photosFilePath = photosFile.getAbsolutePath();
        }
        //list.add(new Photo(R.mipmap.ic_launcher, 10.0, 1.1, "202000101", "this is a caption"));
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public void clickReset(View v) throws IOException {
        showOriginalView();
    }
    //share image
    public void shareImage(View v){
        //ImageView imageView = findViewById(R.id.ivGallery);
        Drawable drawable=imageView.getDrawable();
        Bitmap bitmap=((BitmapDrawable)drawable).getBitmap();
        try {
            String filename = mCurrentPhotoPath.split(getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath(), 2)[1];
            File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath(), filename);
            FileOutputStream fOut = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            fOut.flush();
            fOut.close();
            file.setReadable(true, false);
            final Intent intent = new Intent(android.content.Intent.ACTION_SEND);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Uri photoURI = FileProvider.getUriForFile(getApplicationContext(), BuildConfig.APPLICATION_ID +".fileprovider", file);

            intent.putExtra(Intent.EXTRA_STREAM, photoURI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setType("image/jpg");

            startActivity(Intent.createChooser(intent, "Share image via"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void goToSearch(View view) throws IOException {
        Intent intent = new Intent(this, SearchActivity.class);
        startActivityForResult(intent, SEARCH_ACTIVITY_REQUEST_CODE);
    }

    public void goToEdit(View view) throws IOException {
        Intent intent = new Intent(this, EditActivity.class);
        intent.putExtra("PHOTO", photoListPresenter.getPhoto());
        startActivityForResult(intent, EDIT_ACTIVITY_REQUEST_CODE);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void deletePhoto(View view) throws IOException {
        photoListPresenter.deletePhoto(mCurrentPhotoPath);
        displayPhoto(photoListPresenter.getPhoto());
    }

    public void takePhoto(View v) throws IOException {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, "com.example.android_gallery_app.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }
    public void uploadVideo(View v) throws IOException {
        Intent GalleryIntent = new Intent();
        GalleryIntent.setType("video/*");
        GalleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(GalleryIntent,
                "select video"), SELECT_VIDEO);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void scrollPhotos(View v) {
        Photo photo = null;
        switch (v.getId()) {
            case R.id.btnPrev:
                photo = photoListPresenter.scrollPhotos(false);
                break;
            case R.id.btnNext:
                photo = photoListPresenter.scrollPhotos(true);
                break;
            default:
                break;
        }
        if(photo == null){
            displayPhoto(photoListPresenter.getPhoto());
        } else {
            displayPhoto(photo);
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void addCaption(View v) {
        displayPhoto(photoListPresenter.addCaption(caption.getText().toString()));
    }

    public void sortFiles(View v) {
        photoListPresenter.sortList();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void displayPhoto(Photo photo) {

        if (photo == null) {
            imageView.setImageResource(R.mipmap.ic_launcher);
            caption.setText("");
            time.setText("");
        } else if (photo.getType() != null) {
            mCurrentPhotoPath = photo.getFile();
            time.setText("FILE TYPE: GIF");
            caption.setVisibility(View.INVISIBLE);
            Glide.with(getApplicationContext()).asGif().load(mCurrentPhotoPath).into(imageView);
        }
        else {
            caption.setVisibility(View.VISIBLE);
            mCurrentPhotoPath = photo.getFile();
            imageView.setImageBitmap(BitmapFactory.decodeFile(mCurrentPhotoPath));
            time.setText(photo.getTimeStamp());
            if (photo.getCaption() != null) {
                if (!photo.getCaption().equals("null")) {
                    caption.setText(photo.getCaption());
                } else {
                    caption.setText("");
                }
            } else {
                caption.setText("");
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg",storageDir);
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SEARCH_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                DateFormat format = new SimpleDateFormat("yyyy‐MM‐dd HH:mm:ss");
                Date startTimestamp, endTimestamp;
                try {
                    String from = (String) data.getStringExtra("STARTTIMESTAMP");
                    String to = (String) data.getStringExtra("ENDTIMESTAMP");
                    startTimestamp = format.parse(from);
                    endTimestamp = format.parse(to);
                } catch (Exception ex) {
                    startTimestamp = null;
                    endTimestamp = null;
                }
                String keywords = (String) data.getStringExtra("KEYWORDS");
                String topLeft = data.getStringExtra("TOPLEFT");
                String bottomRight = data.getStringExtra("BOTTOMRIGHT");

                displayPhoto(photoListPresenter.findPhotos_second(startTimestamp, endTimestamp, keywords, topLeft, bottomRight));
            }
        }
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 5);
                return;
            }
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @RequiresApi(api = Build.VERSION_CODES.N)
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                double mLatitude = location.getLatitude();
                                double mLongitude = location.getLongitude();
                                //ImageView mImageView = (ImageView) findViewById(R.id.ivGallery);
                                imageView.setImageBitmap(BitmapFactory.decodeFile(mCurrentPhotoPath));
                                //EditText et = (EditText) findViewById(R.id.etCaption);
                                caption.setText("");
                                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                                Photo photo = new Photo ( mCurrentPhotoPath, mLatitude, mLongitude, timeStamp, "");
                                photoListPresenter.addPhoto(photo, photosFilePath);
                                displayPhoto(photo);
                            }
                        }
                    });
        }
        if (requestCode == SELECT_VIDEO && resultCode == RESULT_OK) {
            Uri selectedVideo = data.getData();

            MediaManager.get()
                    .upload(selectedVideo)
                    .unsigned("zlqihqsa")
                    .option("resource_type", "video")
                    .callback(new UploadCallback() {
                        @Override
                        public void onStart(String requestId) {
                            progressBar.setVisibility(View.VISIBLE);
                            Toast.makeText(MainActivity.this,
                                    "Upload has started ...", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onProgress(String requestId, long bytes, long totalBytes) { }

                        @Override
                        public void onSuccess(String requestId, Map resultData) {
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(MainActivity.this, "Uploaded Succesfully",
                                    Toast.LENGTH_SHORT).show();

                            String publicId = resultData.get("public_id").toString();

                            gifUrl = MediaManager.get().url().resourceType("video")
                                    .transformation(new Transformation().videoSampling("25")
                                            .delay("200").height(200).effect("loop:10").crop("scale"))
                                    .format("gif").generate(publicId);
                            Glide.with(getApplicationContext()).asGif().load(gifUrl).into(imageView);

                            Photo photo = new Photo (gifUrl);
                            photoListPresenter.addPhoto(photo, gifUrl);
                        }

                        @Override
                        public void onError(String requestId, ErrorInfo error) {
                            Toast.makeText(MainActivity.this,
                                    "Upload Error", Toast.LENGTH_SHORT).show();
                            Log.v("ERROR!!", error.getDescription());
                        }

                        @Override
                        public void onReschedule(String requestId, ErrorInfo error) {

                        }
                    }).dispatch();
        }
    }

}