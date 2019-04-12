package com.example.suraj.cameraapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity  {

    private CameraPreview camPreview;
    private FrameLayout mainLayout;
    private int PreviewSizeWidth = 1600;
    private int PreviewSizeHeight = 1200;
    public static final int RequestPermissionCode = 1;
    public static File file;
    public static int count = -1;
    int SCREEN_HEIGHT,SCREEN_WIDTH;
    public static boolean  Flash = true;
    public static File path;
    public static String[] fileNames = new String[count+1];
    ImageButton on,off;
    static final int REQUEST_VIDEO_CAPTURE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        on = (ImageButton) findViewById(R.id.imageButton2);
        off = (ImageButton) findViewById(R.id.imageButton4);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        SCREEN_HEIGHT = dm.heightPixels;
        SCREEN_WIDTH = dm.widthPixels;


        EnableRuntimePermission();

        final int REQUEST_EXTERNAL_STORAGE = 2;
        String[] PERMISSIONS_STORAGE = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );

        }

        SurfaceView camView = new SurfaceView(this);
        SurfaceHolder camHolder = camView.getHolder();
        camPreview = new CameraPreview(PreviewSizeWidth, PreviewSizeHeight);

        camHolder.addCallback(camPreview);
        camHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        mainLayout = (FrameLayout) findViewById(R.id.frameLayout1);
        mainLayout.addView(camView, new FrameLayout.LayoutParams(1080, 1920));

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            int Y = (int) event.getY();
            if (Y<= SCREEN_HEIGHT - 75 )
                //mHandler.postDelayed(TakePicture, 300);
                camPreview.CameraStartAutoFocus();

        }
        return true;
    }

    public void capture (View v) {
        TakePicture.run();
    }

    public void record (View v) {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
        }
    }

    public void setFlash (View v) {
        if (Flash) {
            Flash = false;
            on.setVisibility(View.INVISIBLE);
            off.setVisibility(View.VISIBLE);
            //Toast.makeText(getApplicationContext(), "Flash turned off", Toast.LENGTH_SHORT).show();
        }
        else {
            Flash = true;
            on.setVisibility(View.VISIBLE);
            off.setVisibility(View.INVISIBLE);
            //Toast.makeText(getApplicationContext(), "Flash turned on", Toast.LENGTH_SHORT).show();
        }
    }

    private Runnable TakePicture = new Runnable() {
        public void run() {
            String root = Environment.getExternalStorageDirectory().toString();
            File myDir = new File(root + "/saved_images");
            myDir.mkdirs();

            count++;
            String fname = "mypic" + count + ".jpg";

            file = new File(myDir, fname);
            if (file.exists()) file.delete();
            try {
                file.createNewFile();
                camPreview.CameraTakePicture("dfhdfh");
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    };


        public void EnableRuntimePermission() {

          //  if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
          //          Manifest.permission.CAMERA)) {

          //  } else {

                ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                        Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        RequestPermissionCode);

          //  }

        }




        @Override
        public void onRequestPermissionsResult(int RC, String per[], int[] PResult) {

            switch (RC) {

                case RequestPermissionCode:

                    if (PResult.length > 0 && PResult[0] == PackageManager.PERMISSION_GRANTED) {


                    } else {

                        Toast.makeText(MainActivity.this, "Your application cannot access camera.", Toast.LENGTH_LONG).show();

                    }
                    break;
            }

        }

        public void gallery (View v) {
            path = new File(Environment.getExternalStorageDirectory(),"/saved_images");

            if(path.exists())
            {
                fileNames = path.list();
            }
            if (fileNames.length>0) {
                Intent i = new Intent(this, GalleryActivity.class);
                startActivity(i);
            }

            else
                Toast.makeText(getApplicationContext(),"No image present in the folder",Toast.LENGTH_LONG);
        }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("count",count);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        count = savedInstanceState.getInt("count");
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences pref;
        pref = getSharedPreferences("info", MODE_PRIVATE);

        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("count",count);

        editor.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences shared = getSharedPreferences("info",MODE_PRIVATE);

        count = shared.getInt("count",0);
    }
}

