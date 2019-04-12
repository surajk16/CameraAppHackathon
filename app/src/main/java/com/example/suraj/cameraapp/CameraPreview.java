package com.example.suraj.cameraapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.hardware.camera2.CameraManager;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Policy;
import java.util.List;

import static java.lang.System.out;

/**
 * Created by suraj on 15-07-2017.
 */

public class CameraPreview implements SurfaceHolder.Callback, android.hardware.Camera.PreviewCallback {

    private android.hardware.Camera mCamera = null;
    private int PreviewSizeWidth;
    private int PreviewSizeHeight;
    private String NowPictureFileName;
    private Boolean TakePicture = false;
    android.hardware.Camera.Parameters parameters;
    //private MediaRecorder mMediaRecorder;

    public CameraPreview(int PreviewlayoutWidth, int PreviewlayoutHeight)
    {
        PreviewSizeWidth = PreviewlayoutWidth;
        PreviewSizeHeight = PreviewlayoutHeight;
        Log.i("TAG","1");
    }


    @Override
    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3)
    {
        Log.i("TAG","2");


        parameters = mCamera.getParameters();
        // Set the camera preview size
        List<android.hardware.Camera.Size> ps = parameters.getSupportedPreviewSizes();
        for (int i=0; i<ps.size(); i++)
            Log.i("SIZE",""+ps.get(i).width + " "+ps.get(i).height);
        android.hardware.Camera.Size p = ps.get(2);

        mCamera.setDisplayOrientation(90);
        parameters.setPreviewSize(1280,960);
        parameters.setPictureSize(1920,1080);

        // Turn on the camera flash.
        String NowFlashMode = parameters.getFlashMode();
        if ( NowFlashMode != null ) {
            if (MainActivity.Flash)
                parameters.setFlashMode(android.hardware.Camera.Parameters.FLASH_MODE_ON);
            else
                parameters.setFlashMode(android.hardware.Camera.Parameters.FLASH_MODE_OFF);
        }
        // Set the auto-focus.
        String NowFocusMode = parameters.getFocusMode ();
        if ( NowFocusMode != null )
            parameters.setFocusMode("auto");

        mCamera.setParameters(parameters);
        //mCamera.startFaceDetection();
        mCamera.startSmoothZoom(2);

        mCamera.startPreview();
    }

    @Override
    public void surfaceCreated(SurfaceHolder arg0)
    {
        Log.i("TAG","3");
        mCamera = android.hardware.Camera.open();
        try
        {
            // If did not set the SurfaceHolder, the preview area will be black.
            mCamera.setPreviewDisplay(arg0);
            mCamera.setPreviewCallback(this);
        }
        catch (IOException e)
        {
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder arg0)
    {
        Log.i("TAG","4");
        mCamera.setPreviewCallback(null);
        mCamera.stopPreview();
        mCamera.release();
        mCamera = null;
    }

    // Take picture interface
    public void CameraTakePicture(String FileName)
    {
        Log.i("TAG","5");
        TakePicture = true;
        NowPictureFileName = FileName;
        mCamera.autoFocus(myAutoFocusCallback);
    }
/*
    public void initRecorder(Surface surface, String FileName) throws IOException {
        // It is very important to unlock the camera before doing setCamera
        // or it will results in a black preview
        if(mCamera == null) {
            mCamera = android.hardware.Camera.open();
            mCamera.unlock();
        }

        if(mMediaRecorder == null)  mMediaRecorder = new MediaRecorder();
        mMediaRecorder.setPreviewDisplay(surface);
        mMediaRecorder.setCamera(mCamera);

        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.DEFAULT);
        //       mMediaRecorder.setOutputFormat(8);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        mMediaRecorder.setVideoEncodingBitRate(512 * 1000);
        mMediaRecorder.setVideoFrameRate(30);
        mMediaRecorder.setVideoSize(640, 480);
        mMediaRecorder.setOutputFile(FileName);

        try {
            mMediaRecorder.prepare();
        } catch (IllegalStateException e) {
            // This is thrown if the previous calls are not called with the
            // proper order
            e.printStackTrace();
        }
    }*/

    // Set auto-focus interface
    public void CameraStartAutoFocus()
    {
        Log.i("TAG","6");
        TakePicture = false;
        mCamera.autoFocus(myAutoFocusCallback);
    }


    //=================================
    //
    // AutoFocusCallback
    //
    //=================================
    android.hardware.Camera.AutoFocusCallback myAutoFocusCallback = new android.hardware.Camera.AutoFocusCallback()
    {

        @Override
        public void onAutoFocus(boolean arg0, android.hardware.Camera NowCamera) {

            Log.i("TAG","7");
            if (!MainActivity.Flash)
            parameters.setFlashMode(android.hardware.Camera.Parameters.FLASH_MODE_OFF);

            else
                parameters.setFlashMode(android.hardware.Camera.Parameters.FLASH_MODE_ON);

            mCamera.setParameters(parameters);


            if ( TakePicture )
            {
                Log.i("TAG","8");
                //NowCamera.stopPreview();//fixed for Samsung S2
                NowCamera.takePicture(shutterCallback, rawPictureCallback, jpegPictureCallback);
                TakePicture = false;
            }
        }

    };
    android.hardware.Camera.ShutterCallback shutterCallback = new android.hardware.Camera.ShutterCallback()
    {
        public void onShutter()
        {
            // Just do nothing.
        }
    };

    android.hardware.Camera.PictureCallback rawPictureCallback = new android.hardware.Camera.PictureCallback()
    {
        @Override
        public void onPictureTaken(byte[] arg0, android.hardware.Camera arg1) {

        }

    };

    android.hardware.Camera.PictureCallback jpegPictureCallback = new android.hardware.Camera.PictureCallback()
    {
        @Override
        public void onPictureTaken(byte[] data, android.hardware.Camera arg1) {
            Log.i("TAG","9");
            Log.i("TAG","10");

            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0,data.length);
            FileOutputStream out = null;
            try {
                out = new FileOutputStream(MainActivity.file);

            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            }

                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);

            try {
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            TakePicture = true;
            mCamera.startPreview();

        }

        };

    @Override
    public void onPreviewFrame(byte[] data, android.hardware.Camera camera) {

    }
}
