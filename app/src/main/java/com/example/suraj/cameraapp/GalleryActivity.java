package com.example.suraj.cameraapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

import static android.R.attr.path;
import static com.example.suraj.cameraapp.MainActivity.fileNames;

public class GalleryActivity extends AppCompatActivity {
    ImageView iv,up,down;
    GestureDetector gestureDetector;
    int i;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        iv = (ImageView) findViewById(R.id.imageView);
        up = (ImageView) findViewById(R.id.imageView2);
        down = (ImageView) findViewById(R.id.imageView3);

        i = fileNames.length-1;

            Bitmap mBitmap = BitmapFactory.decodeFile(MainActivity.path.getPath()+"/"+ fileNames[i]);
            mBitmap = RotateBitmap(mBitmap,90);
            iv.setImageBitmap(mBitmap);
    }

    public Bitmap RotateBitmap(Bitmap source, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    public void prevImage (View v) {
        if (i>0) {
            i--;
            Bitmap mBitmap = BitmapFactory.decodeFile(MainActivity.path.getPath()+"/"+ fileNames[i]);
            mBitmap = RotateBitmap(mBitmap,90);
            iv.setImageBitmap(mBitmap);
        }
    }

    public void nextImage (View v) {
        if (i!= fileNames.length-1) {
            i++;
            Bitmap mBitmap = BitmapFactory.decodeFile(MainActivity.path.getPath()+"/"+ fileNames[i]);
            mBitmap = RotateBitmap(mBitmap,90);
            iv.setImageBitmap(mBitmap);
        }
    }

    public void delete (View v) {
        File temp = new File (MainActivity.path.getPath()+"/"+ fileNames[i]);
        temp.delete();
        Toast.makeText(getApplicationContext(),"Image deleted succesfully",Toast.LENGTH_SHORT);

        MainActivity.path = new File(Environment.getExternalStorageDirectory(),"/saved_images");

        if(MainActivity.path.exists())
        {
            MainActivity.fileNames = MainActivity.path.list();
        }
        if (MainActivity.fileNames.length>0) {
            i = fileNames.length-1;

            Bitmap mBitmap = BitmapFactory.decodeFile(MainActivity.path.getPath()+"/"+ fileNames[i]);
            mBitmap = RotateBitmap(mBitmap,90);
            iv.setImageBitmap(mBitmap);
        }

        else {
            Intent i = new Intent(this,MainActivity.class);
            startActivity(i);
        }

    }


}
