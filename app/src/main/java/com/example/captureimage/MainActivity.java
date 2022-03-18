package com.example.captureimage;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    //initialize variable
    ImageView imageView;
    Button btOpen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Assign variable
        imageView = findViewById(R.id.image_view);
        btOpen = findViewById(R.id.bt_open);

        //Request for camera permission
        if(ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{
                            Manifest.permission.CAMERA
                    },100);
        }

        //Request permission for saving images
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

        btOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Open Camera
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 100);
            }
        });

        /*btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //store image to gallery
                saveToGallery();
            }
        }); */
    }

    String currentPhotoPath;
    private File createImageFile() throws IOException{
        //create an Image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, /* prefix */
                                         ".jpg",
                                         storageDir);
        //save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void TakePictureIntent(){
        Intent takePictureintent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //ensure that there's a camera activity to handle the intent
        if (takePictureintent.resolveActivity(getPackageManager()) != null) {
            //create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoUri;
                photoUri = FileProvider.getUriForFile(this, getString(R.string.my_pictures),
                        photoFile);
                takePictureintent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(takePictureintent, 100);
            }
        }

    }

    private void saveToGallery(){
        Intent mediaSCanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(currentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaSCanIntent.setData(contentUri);
        this.sendBroadcast(mediaSCanIntent);
    }


    /* private void saveToGallery() {
        //create a bitmap drawable and called an imageview in that bitpmap drawable
        BitmapDrawable bitmapDrawable = (BitmapDrawable) imageView.getDrawable();
        //collecting the bitmap drawable into a bitmap
        Bitmap bitmap = bitmapDrawable.getBitmap();

        FileOutputStream outputStream = null;
        File file = Environment.getExternalStorageDirectory();
        //create a folder in our storage to save our images
        File dir = new File(file.getAbsolutePath() + "/MyImages");
        //create the directories
        dir.mkdir();

        //Store the images into a format file
        String filename = String.format("%d.png", System.currentTimeMillis());

        File outFile = new File(dir,filename);
        try {
            outputStream = new FileOutputStream(outFile);

        } catch (Exception e) {
            e.printStackTrace();
        }
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);

        try {
            outputStream.flush();
        } catch(Exception e){
            e.printStackTrace();
        }

        try {
            outputStream.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            //Get Capture Image
            Bitmap captureImage = (Bitmap) data.getExtras().get("data");
            //set capture image to ImageView
            imageView.setImageBitmap(captureImage);
        }
    }
}